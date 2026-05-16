package org.baze.neuronauka.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SesijaIzmenaView {

    // ── read-only prikaz ──────────────────────────────────────────
    private Label idLabel          = new Label();

    // ── editable polja ────────────────────────────────────────────
    private DatePicker datumPicker = new DatePicker();

    private Spinner<Integer> pocetakHH = new Spinner<>(0, 23, 8);
    private Spinner<Integer> pocetakMM = new Spinner<>(0, 59, 0);
    private Spinner<Integer> krajHH    = new Spinner<>(0, 23, 9);
    private Spinner<Integer> krajMM    = new Spinner<>(0, 59, 0);

    private ComboBox<String> tipSesijeBox = new ComboBox<>();

    // ── info polja (read-only prikaz konteksta) ───────────────────
    private Label eksperimentLabel  = new Label();
    private Label laboratorijaLabel = new Label();

    // ── dugmad ───────────────────────────────────────────────────
    private Button saveBtn   = new Button("Save changes");
    private Button cancelBtn = new Button("Ostavi kako je bilo");

    // ── poruka o grešci / uspehu ──────────────────────────────────
    private Label statusLabel = new Label();

    // ── referenca na dialog Stage (da ga controller može zatvoriti) ─
    private Stage dialogStage;

    // ─────────────────────────────────────────────────────────────
    public void show(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Izmena sesije");
        dialogStage.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f5f5f5;");

        root.getChildren().addAll(
                buildHeader(),
                buildBody(),
                buildFooter(dialogStage)
        );

        Scene scene = new Scene(root, 420, 500);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    // ─────────────────────────────────────────────────────────────
    // HEADER — ID sesije (read-only)
    // ─────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        Label naslov = new Label("Izmena sesije");
        naslov.setFont(Font.font("System", FontWeight.BOLD, 15));

        idLabel.setStyle(
                "-fx-text-fill: #aaaaaa;" +
                        "-fx-font-size: 13px;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, naslov, spacer, idLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 18, 14, 18));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return header;
    }

    // ─────────────────────────────────────────────────────────────
    // BODY — sva editabilna i info polja
    // ─────────────────────────────────────────────────────────────
    private VBox buildBody() {
        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 18, 10, 18));

        // kontekst — read only
        body.getChildren().add(buildInfoRow("Eksperiment:", eksperimentLabel));
        body.getChildren().add(buildInfoRow("Laboratorija:", laboratorijaLabel));

        body.getChildren().add(buildSeparator());

        // datum
        body.getChildren().add(buildFieldLabel("Datum sesije"));
        datumPicker.setMaxWidth(Double.MAX_VALUE);
        datumPicker.setPromptText("Odaberi datum");
        datumPicker.setStyle("-fx-font-size: 13px;");
        body.getChildren().add(datumPicker);

        // vreme početka
        body.getChildren().add(buildFieldLabel("Vreme početka"));
        body.getChildren().add(buildVremeRow(pocetakHH, pocetakMM));

        // vreme kraja
        body.getChildren().add(buildFieldLabel("Vreme kraja"));
        body.getChildren().add(buildVremeRow(krajHH, krajMM));

        // tip sesije
        body.getChildren().add(buildFieldLabel("Tip sesije"));
        tipSesijeBox.getItems().addAll("baseline", "stimulus", "recovery", "wash_out");
        tipSesijeBox.setMaxWidth(Double.MAX_VALUE);
        tipSesijeBox.setStyle("-fx-font-size: 13px;");
        body.getChildren().add(tipSesijeBox);

        // status poruka (greška / uspeh)
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cc3333;");
        statusLabel.setWrapText(true);
        body.getChildren().add(statusLabel);

        return body;
    }

    // ─────────────────────────────────────────────────────────────
    // FOOTER — dugmad
    // ─────────────────────────────────────────────────────────────
    private HBox buildFooter(Stage dialog) {
        styleSaveBtn();
        styleCancelBtn();

        // cancel samo zatvara prozor — nema izmena
        cancelBtn.setOnAction(e -> dialog.close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(10, cancelBtn, spacer, saveBtn);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(14, 18, 14, 18));
        footer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1 0 0 0;"
        );
        return footer;
    }

    // ─────────────────────────────────────────────────────────────
    // POMOĆNE METODE ZA GRADNJU UI
    // ─────────────────────────────────────────────────────────────

    /** Red sa labelom i read-only vrednošću (eksperiment, laboratorija). */
    private HBox buildInfoRow(String tekst, Label vrednost) {
        Label kljuc = new Label(tekst);
        kljuc.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
        kljuc.setMinWidth(100);

        vrednost.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

        HBox row = new HBox(8, kljuc, vrednost);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Mala label iznad polja. */
    private Label buildFieldLabel(String tekst) {
        Label l = new Label(tekst);
        l.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #888888;"
        );
        return l;
    }

    /** Red sa HH spinner : MM spinner za unos vremena. */
    private HBox buildVremeRow(Spinner<Integer> hh, Spinner<Integer> mm) {
        hh.setEditable(true);
        mm.setEditable(true);
        hh.setPrefWidth(72);
        mm.setPrefWidth(72);
        hh.setStyle("-fx-font-size: 13px;");
        mm.setStyle("-fx-font-size: 13px;");

        // vodeća nula za prikaz (08, 09...)
        StringConverter<Integer> dvaBroja = new javafx.util.StringConverter<>() {
            public String toString(Integer v)   { return v == null ? "00" : String.format("%02d", v); }
            public Integer fromString(String s) {
                try { return Integer.parseInt(s.trim()); }
                catch (NumberFormatException e) { return 0; }
            }
        };
        hh.getValueFactory().setConverter(dvaBroja);
        mm.getValueFactory().setConverter(dvaBroja);

        Label dvotacka = new Label(":");
        dvotacka.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555555;");

        HBox row = new HBox(6, hh, dvotacka, mm);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Separator buildSeparator() {
        Separator sep = new Separator();
        sep.setPadding(new Insets(2, 0, 2, 0));
        return sep;
    }

    private void styleSaveBtn() {
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(
                "-fx-background-color: #534AB7;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-cursor: hand;"
        );
        saveBtn.setOnMouseEntered(e ->
                saveBtn.setStyle(saveBtn.getStyle().replace("#534AB7", "#3C3489")));
        saveBtn.setOnMouseExited(e ->
                saveBtn.setStyle(saveBtn.getStyle().replace("#3C3489", "#534AB7")));
    }

    private void styleCancelBtn() {
        cancelBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #888888;" +
                        "-fx-font-size: 13px;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 20 8 20;" +
                        "-fx-cursor: hand;"
        );
        cancelBtn.setOnMouseEntered(e ->
                cancelBtn.setStyle(cancelBtn.getStyle().replace(
                        "-fx-background-color: transparent;", "-fx-background-color: #f0f0f0;")));
        cancelBtn.setOnMouseExited(e ->
                cancelBtn.setStyle(cancelBtn.getStyle().replace(
                        "-fx-background-color: #f0f0f0;", "-fx-background-color: transparent;")));
    }

    // ─────────────────────────────────────────────────────────────
    // GETTERI — controller puni polja i vezuje save akciju
    // ─────────────────────────────────────────────────────────────
    public Label            getIdLabel()          { return idLabel; }
    public DatePicker       getDatumPicker()       { return datumPicker; }
    public Spinner<Integer> getPocetakHH()         { return pocetakHH; }
    public Spinner<Integer> getPocetakMM()         { return pocetakMM; }
    public Spinner<Integer> getKrajHH()            { return krajHH; }
    public Spinner<Integer> getKrajMM()            { return krajMM; }
    public ComboBox<String> getTipSesijeBox()      { return tipSesijeBox; }
    public Label            getEksperimentLabel()  { return eksperimentLabel; }
    public Label            getLaboratorijaLabel() { return laboratorijaLabel; }
    public Button           getSaveBtn()           { return saveBtn; }
    public Button           getCancelBtn()         { return cancelBtn; }
    public Label            getStatusLabel()       { return statusLabel; }
    public Stage            getDialogStage()       { return dialogStage; }
}