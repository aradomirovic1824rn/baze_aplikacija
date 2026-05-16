package org.baze.neuronauka.controller;

import org.baze.neuronauka.dao.SesijaDAO;
import org.baze.neuronauka.model.SesijaDTO;
import org.baze.neuronauka.view.SesijaIzmenaView;

import java.sql.SQLException;
import java.time.LocalTime;

public class SesijaIzmenaController {

    private final SesijaIzmenaView view;
    private final SesijaDAO        dao;
    private final int              idSesija;

    // Callback koji SesijaController prosleđuje da bi osvežio
    // kalendar nakon uspešnog save-a
    private final Runnable onSaveSuccess;

    // ─────────────────────────────────────────────────────────────
    public SesijaIzmenaController(SesijaIzmenaView view,
                                  SesijaDTO sesija,
                                  Runnable onSaveSuccess) {
        this.view          = view;
        this.dao           = new SesijaDAO();
        this.idSesija      = sesija.getIdSesija();
        this.onSaveSuccess = onSaveSuccess;

        initActions();
    }

    // ─────────────────────────────────────────────────────────────
    // INICIJALIZACIJA
    // ─────────────────────────────────────────────────────────────
    private void initActions() {
        view.getSaveBtn().setOnAction(e -> handleSave());
        // cancelBtn je već vezan u view-u (zatvara dialog)
    }

    // ─────────────────────────────────────────────────────────────
    // SAVE LOGIKA
    // ─────────────────────────────────────────────────────────────
    private void handleSave() {
        // 1. Očisti prethodnu poruku
        setStatus("", false);

        // 2. Čitaj vrednosti iz forme
        var datum   = view.getDatumPicker().getValue();
        int pocHH   = view.getPocetakHH().getValue();
        int pocMM   = view.getPocetakMM().getValue();
        int krajHH  = view.getKrajHH().getValue();
        int krajMM  = view.getKrajMM().getValue();
        String tip  = view.getTipSesijeBox().getValue();

        // 3. Klijentska validacija — pre nego što idemo u bazu
        if (datum == null) {
            setStatus("Datum nije odabran.", true);
            return;
        }
        if (tip == null || tip.isBlank()) {
            setStatus("Tip sesije nije odabran.", true);
            return;
        }

        LocalTime pocetak = LocalTime.of(pocHH, pocMM);
        LocalTime kraj    = LocalTime.of(krajHH, krajMM);

        if (!kraj.isAfter(pocetak)) {
            setStatus("Vreme kraja mora biti posle vremena početka.", true);
            return;
        }

        // 4. Pokušaj UPDATE — ako trigger detektuje preklapanje, leti SQLException
        try {
            dao.updateSesija(idSesija, datum, pocetak, kraj, tip);

            // Uspeh — prikaži zelenu poruku, osveži kalendar, zatvori dialog
            setStatus("Izmene sačuvane.", false);
            onSaveSuccess.run();           // osvežava kalendar u pozadini
            view.getDialogStage().close(); // zatvori modal

        } catch (SQLException ex) {
            // Trigger u bazi je odbio UPDATE zbog preklapanja
            // Poruka triggera dolazi kroz ex.getMessage()
            String msg = ex.getMessage();

            if (msg != null && msg.toLowerCase().contains("overlap")) {
                setStatus("Sesija se preklapa sa drugom sesijom u ovoj laboratoriji.", true);
            } else {
                // Neka druga greška baze — prikaži raw poruku da lakše debuguješ
                setStatus("Greška baze: " + msg, true);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POMOĆNA — postavlja statusLabel boju i tekst
    // ─────────────────────────────────────────────────────────────
    private void setStatus(String tekst, boolean jeGreska) {
        view.getStatusLabel().setText(tekst);
        view.getStatusLabel().setStyle(
                "-fx-font-size: 12px; -fx-wrap-text: true;" +
                        (jeGreska
                                ? "-fx-text-fill: #cc3333;"   // crvena za grešku
                                : "-fx-text-fill: #2e7d32;")  // zelena za uspeh
        );
    }
}