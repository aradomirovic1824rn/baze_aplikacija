package org.baze.neuronauka.controller;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.baze.neuronauka.dao.SesijaDAO;
import org.baze.neuronauka.model.SesijaDTO;
import org.baze.neuronauka.view.LaboratorijaView;
import org.baze.neuronauka.view.SesijaIzmenaView;
import org.baze.neuronauka.view.SesijaView;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SesijaController {

    private final SesijaView view;
    private final SesijaDAO  dao = new SesijaDAO();
    private String loggedInUser = "";

    // ─────────────────────────────────────────────────────────────
    public SesijaController(SesijaView view, String loggedInUser) {
        this.view          = view;
        this.loggedInUser  = loggedInUser;
        ucitajDropdownove();
        initActions();
        osvežiKalendar();
    }

    // ─────────────────────────────────────────────────────────────
    // INICIJALIZACIJA
    // ─────────────────────────────────────────────────────────────

    private void ucitajDropdownove() {
        String odabranaLab = view.getLabFilter().getValue();
        view.getLabFilter().getItems().clear();
        view.getLabFilter().getItems().add(null);
        view.getLabFilter().getItems().addAll(dao.getLaboratorije());
        if (odabranaLab != null && view.getLabFilter().getItems().contains(odabranaLab))
            view.getLabFilter().setValue(odabranaLab);
        else
            view.getLabFilter().getSelectionModel().selectFirst();

        String odabraniExp = view.getExpFilter().getValue();
        view.getExpFilter().getItems().clear();
        view.getExpFilter().getItems().add(null);
        view.getExpFilter().getItems().addAll(dao.getEksperimenti());
        if (odabraniExp != null && view.getExpFilter().getItems().contains(odabraniExp))
            view.getExpFilter().setValue(odabraniExp);
        else
            view.getExpFilter().getSelectionModel().selectFirst();
    }
    private void otvoriUpravljanjeLaboratorijama() {
        LaboratorijaView labView = new LaboratorijaView();
        new org.baze.neuronauka.controller.LaboratorijaController(
                labView,
                this::ucitajDropdownove
        );
        Stage parentStage = (Stage) view.getPrevMonthBtn().getScene().getWindow();
        labView.show(parentStage, loggedInUser);   // ← prosleđuje korisnika
    }

    private void initActions() {
        view.getPrevMonthBtn().setOnAction(e -> {
            view.setCurrentYearMonth(view.getCurrentYearMonth().minusMonths(1));
            osvežiKalendar();
        });

        view.getNextMonthBtn().setOnAction(e -> {
            view.setCurrentYearMonth(view.getCurrentYearMonth().plusMonths(1));
            osvežiKalendar();
        });

        view.getYearSpinner().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            YearMonth novi = YearMonth.of(newVal, view.getCurrentYearMonth().getMonth());
            view.setCurrentYearMonth(novi);
            osvežiKalendar();
        });

        // svaka promena filtera ide ponovo u bazu
        view.getLabFilter().setOnAction(e -> osvežiKalendar());
        view.getExpFilter().setOnAction(e -> osvežiKalendar());
        view.getEditLabBtn().setOnAction(e -> otvoriUpravljanjeLaboratorijama());

    }

    // ─────────────────────────────────────────────────────────────
    // GLAVNI REFRESH — poziva DAO, pa renderuje
    // ─────────────────────────────────────────────────────────────

    private void osvežiKalendar() {
        String odabranaLab = view.getLabFilter().getValue();
        String odabraniExp = view.getExpFilter().getValue();

        // Ako je odabrano "sve" (null vrednost), šaljemo null → SQL ignoriše filter
        List<SesijaDTO> sesije = dao.getSesijeFiltered(odabranaLab, odabraniExp);

        // Ako još nije postavljen mesec, skači na poslednji mesec koji ima sesije
        if (!sesije.isEmpty()) {
            YearMonth trenutni = view.getCurrentYearMonth();
            boolean imaUTrenutnom = sesije.stream()
                    .anyMatch(s -> YearMonth.from(s.getDatumSesije()).equals(trenutni));

            if (!imaUTrenutnom) {
                // skači na poslednji mesec sa sesijama
                sesije.stream()
                        .map(s -> YearMonth.from(s.getDatumSesije()))
                        .max(Comparator.naturalOrder())
                        .ifPresent(view::setCurrentYearMonth);
            }
        }

        renderKalendar(sesije);
        renderSidebar(sesije);
    }

    // ─────────────────────────────────────────────────────────────
    // RENDERING KALENDARA
    // ─────────────────────────────────────────────────────────────

    private void renderKalendar(List<SesijaDTO> sveSesjeSaFiltera) {
        view.clearCalendarGrid();

        YearMonth ym       = view.getCurrentYearMonth();
        LocalDate danas    = LocalDate.now();
        int ukupnoDana     = ym.lengthOfMonth();
        // koliko praznih ćelija pre prvog (0=Pon, 6=Ned)
        int offset         = ym.atDay(1).getDayOfWeek().getValue() - 1;

        // samo sesije koje padaju u ovaj mesec
        List<SesijaDTO> ovogMeseca = sveSesjeSaFiltera.stream()
                .filter(s -> YearMonth.from(s.getDatumSesije()).equals(ym))
                .collect(Collectors.toList());

        // prazne ćelije pre prvog dana
        for (int i = 0; i < offset; i++) {
            view.addCellToGrid(view.makeEmptyCell(), i, 0);
        }

        int col = offset;
        int row = 0;

        for (int dan = 1; dan <= ukupnoDana; dan++) {
            LocalDate datum = ym.atDay(dan);
            boolean jeToday = datum.equals(danas);

            VBox cell = view.makeDayCell(dan, jeToday);

            // sesije za ovaj konkretan dan, sortirane po vremenu
            ovogMeseca.stream()
                    .filter(s -> s.getDatumSesije().equals(datum))
                    .sorted(Comparator.comparing(SesijaDTO::getVremePocetka))
                    .forEach(s -> {
                        int trajMin = (int) Duration.between(
                                s.getVremePocetka(), s.getVremeKraja()).toMinutes();

                        VBox chip = view.makeSesijaChip(
                                s.getIdSesija(),
                                s.getVremePocetka().toString(),
                                trajMin,
                                s.getEksperiment(),
                                s.getTipSesije()
                        );

                        chip.setOnMouseClicked(e -> otvoriIzmenu(s));

                        cell.getChildren().add(chip);
                    });

            view.addCellToGrid(cell, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // RENDERING SIDEBARA
    // Prikazuje eksperimente ovog meseca, a u zagradi ID-jevi sesija
    // Primer: "EXP-001 Reakcija na stimul  (sesije: 14, 19)"
    // ─────────────────────────────────────────────────────────────

    private void renderSidebar(List<SesijaDTO> sveSesjeSaFiltera) {
        view.getSesijaListView().getItems().clear();

        YearMonth ym = view.getCurrentYearMonth();

        // filtriraj samo ovaj mesec
        List<SesijaDTO> ovogMeseca = sveSesjeSaFiltera.stream()
                .filter(s -> YearMonth.from(s.getDatumSesije()).equals(ym))
                .collect(Collectors.toList());

        if (ovogMeseca.isEmpty()) {
            view.getSesijaListView().getItems().add("Nema eksperimenata za ovaj mesec.");
            return;
        }

        // Grupiši po nazivu eksperimenta — cela logika grupisanja u Javi
        // (GROUP BY je već u SQL-u za agregirane upite; ovde je UI grupisanje za prikaz)
        ovogMeseca.stream()
                .map(SesijaDTO::getEksperiment)
                .distinct()
                .sorted()
                .forEach(eksperiment -> {
                    // svi ID-jevi sesija vezani za ovaj eksperiment ovog meseca
                    String idSesija = ovogMeseca.stream()
                            .filter(s -> eksperiment.equals(s.getEksperiment()))
                            .sorted(Comparator.comparing(SesijaDTO::getDatumSesije)
                                    .thenComparing(SesijaDTO::getVremePocetka))
                            .map(s -> String.valueOf(s.getIdSesija()))
                            .collect(Collectors.joining(", "));

                    view.getSesijaListView().getItems()
                            .add(eksperiment + "  (sesije: " + idSesija + ")");
                });
    }

    // ─────────────────────────────────────────────────────────────
    // OTVARANJE FORME ZA IZMENU — popuni polja iz DTO-a, pa prikaži
    // ─────────────────────────────────────────────────────────────
    private void otvoriIzmenu(SesijaDTO s) {
        SesijaIzmenaView izmenaView = new SesijaIzmenaView();

        // popuni polja pre nego što se prozor prikaže
        izmenaView.getIdLabel().setText("ID sesije: " + s.getIdSesija());
        izmenaView.getEksperimentLabel().setText(s.getEksperiment());
        izmenaView.getLaboratorijaLabel().setText(s.getLaboratorija());
        izmenaView.getDatumPicker().setValue(s.getDatumSesije());
        izmenaView.getPocetakHH().getValueFactory().setValue(s.getVremePocetka().getHour());
        izmenaView.getPocetakMM().getValueFactory().setValue(s.getVremePocetka().getMinute());
        izmenaView.getKrajHH().getValueFactory().setValue(s.getVremeKraja().getHour());
        izmenaView.getKrajMM().getValueFactory().setValue(s.getVremeKraja().getMinute());
        izmenaView.getTipSesijeBox().setValue(s.getTipSesije());

        // controller mora da postoji PRE show() jer show() crta dugmad
        // a controller vezuje saveBtn akciju
        new SesijaIzmenaController(izmenaView, s, this::osvežiKalendar);

        Stage parentStage = (Stage) view.getPrevMonthBtn().getScene().getWindow();
        izmenaView.show(parentStage);
    }
}