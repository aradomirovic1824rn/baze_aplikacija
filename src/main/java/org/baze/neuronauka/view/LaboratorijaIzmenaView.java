package org.baze.neuronauka.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.baze.neuronauka.model.LaboratorijaDTO;

public class LaboratorijaIzmenaView {

    private final Label     idLabel    = new Label();
    private final TextField nazivField = new TextField();
    private final TextField tipField   = new TextField();
    private final TextArea  opisField  = new TextArea();
    private final Button    sacuvajBtn = new Button("Sačuvaj izmene");
    private final Button    otkaziBtn  = new Button("Otkaži");

    private Stage dialog;

    public void show(Stage parentStage, LaboratorijaDTO dto) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Izmena laboratorije");
        dialog.setResizable(false);

        // popuni polja
        idLabel.setText("ID: " + dto.getIdLaboratorija());
        idLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");
        nazivField.setText(dto.getNaziv());
        tipField.setText(dto.getTipLaboratorije() != null ? dto.getTipLaboratorije() : "");
        opisField.setText(dto.getOpisLokacije()   != null ? dto.getOpisLokacije()    : "");

        opisField.setPrefRowCount(3);
        opisField.setWrapText(true);

        sacuvajBtn.setStyle(
                "-fx-background-color: #534AB7; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-background-radius: 6;" +
                        "-fx-cursor: hand; -fx-padding: 6 18 6 18;"
        );
        otkaziBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #888888;" +
                        "-fx-font-size: 12px; -fx-cursor: hand;" +
                        "-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-padding: 6 14 6 14;"
        );

        otkaziBtn.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(8, sacuvajBtn, otkaziBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(8, 0, 0, 0));

        VBox forma = new VBox(12,
                idLabel,
                makeFormRow("Naziv",         nazivField),
                makeFormRow("Tip",           tipField),
                makeFormRow("Opis lokacije", opisField),
                btnRow
        );
        forma.setPadding(new Insets(20));
        forma.setStyle("-fx-background-color: white;");
        forma.setPrefWidth(380);

        dialog.setScene(new Scene(forma));
        dialog.show();
    }

    public void close() { dialog.close(); }

    // getteri
    public TextField getNazivField() { return nazivField; }
    public TextField getTipField()   { return tipField; }
    public TextArea  getOpisField()  { return opisField; }
    public Button    getSacuvajBtn() { return sacuvajBtn; }

    private HBox makeFormRow(String labelTekst, Control field) {
        Label lbl = new Label(labelTekst);
        lbl.setMinWidth(100);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        HBox row = new HBox(10, lbl, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        field.setMaxWidth(Double.MAX_VALUE);
        return row;
    }
}