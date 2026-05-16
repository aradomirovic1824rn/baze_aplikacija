package org.baze.neuronauka.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.baze.neuronauka.dao.LaboratorijaDAO;
import org.baze.neuronauka.model.LaboratorijaDTO;
import org.baze.neuronauka.view.LaboratorijaIzmenaView;
import org.baze.neuronauka.view.LaboratorijaView;

import java.sql.SQLException;
import java.util.Optional;

public class LaboratorijaController {

    private final LaboratorijaView view;
    private final LaboratorijaDAO  dao = new LaboratorijaDAO();

    // callback kojim obaveštavamo SesijaController da osveži dropdown
    private final Runnable onOsvezi;

    // ─────────────────────────────────────────────────────────────
    // KONSTRUKTOR
    // onOsvezi — SesijaController prosleđuje this::ucitajDropdownove
    // ─────────────────────────────────────────────────────────────
    public LaboratorijaController(LaboratorijaView view, Runnable onOsvezi) {
        this.view     = view;
        this.onOsvezi = onOsvezi;

        view.setIzmeniCallback(this::otvoriIzmenu);
        view.setObrisiCallback(this::pokusajBrisanje);

        ucitajListu();
    }

    // ─────────────────────────────────────────────────────────────
    // PUNJENJE LISTE
    // ─────────────────────────────────────────────────────────────
    private void ucitajListu() {
        view.getListaView().getItems().setAll(dao.getSveLaboratorije());
    }


    // ─────────────────────────────────────────────────────────────
    // OTVORI MODAL FORMU ZA IZMENU
    // ─────────────────────────────────────────────────────────────
    private void otvoriIzmenu(LaboratorijaDTO dto) {
        LaboratorijaIzmenaView izmenaView = new LaboratorijaIzmenaView();
        izmenaView.show(view.getStage(), dto);

        izmenaView.getSacuvajBtn().setOnAction(e -> {
            String naziv = izmenaView.getNazivField().getText().trim();
            String tip   = izmenaView.getTipField().getText().trim();
            String opis  = izmenaView.getOpisField().getText().trim();

            if (naziv.isEmpty()) {
                prikaziGresku("Naziv ne sme biti prazan.");
                return;
            }
            try {
                dao.updateLaboratorija(dto.getIdLaboratorija(),
                        naziv,
                        tip.isEmpty()  ? null : tip,
                        opis.isEmpty() ? null : opis);

                izmenaView.close();
                ucitajListu();
                if (onOsvezi != null) onOsvezi.run();

            } catch (SQLException ex) {
                prikaziGresku("Greška pri čuvanju: " + ex.getMessage());
            }
        });
    }



    // ─────────────────────────────────────────────────────────────
    // POKUŠAJ BRISANJA — provera pa potvrda
    // ─────────────────────────────────────────────────────────────
    private void pokusajBrisanje(LaboratorijaDTO dto) {

        if (dao.imaIzvodjenja(dto.getIdLaboratorija())) {
            prikaziGresku(
                    "Laboratorija \"" + dto.getNaziv() + "\" ne može biti obrisana.\n\n" +
                            "U njoj postoje izvođenja eksperimenata.\nBrisanje nije dozvoljeno."
            );
            return;
        }

        Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
        potvrda.setTitle("Brisanje laboratorije");
        potvrda.setHeaderText("Obriši \"" + dto.getNaziv() + "\"?");
        potvrda.setContentText(
                "ID:   " + dto.getIdLaboratorija() + "\n" +
                        "Tip:  " + (dto.getTipLaboratorije() != null ? dto.getTipLaboratorije() : "—") + "\n" +
                        "Opis: " + (dto.getOpisLokacije()   != null ? dto.getOpisLokacije()    : "—") + "\n\n" +
                        "Ova akcija je nepovratna."
        );

        Optional<ButtonType> rezultat = potvrda.showAndWait();
        if (rezultat.isEmpty() || rezultat.get() != ButtonType.OK) return;

        try {
            dao.deleteLaboratorija(dto.getIdLaboratorija());
            ucitajListu();
            if (onOsvezi != null) onOsvezi.run();
        } catch (SQLException ex) {
            prikaziGresku("Greška pri brisanju: " + ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    private void prikaziGresku(String poruka) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }
}