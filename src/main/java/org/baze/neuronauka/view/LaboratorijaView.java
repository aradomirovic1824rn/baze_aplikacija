package org.baze.neuronauka.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.baze.neuronauka.model.LaboratorijaDTO;

import java.util.Objects;
import java.util.function.Consumer;

public class LaboratorijaView {

    // ─── lista ───────────────────────────────────────────────────
    private final ListView<LaboratorijaDTO> listaView = new ListView<>();



    // ─── callback-i koje controller postavlja ────────────────────
    private Consumer<LaboratorijaDTO> izmeniCallback;
    private Consumer<LaboratorijaDTO> obrisiCallback;

    private Stage stage;
    private String loggedInUser = "";

    // ─────────────────────────────────────────────────────────────
    // SHOW
    // ─────────────────────────────────────────────────────────────
    public void show(Stage parentStage, String korisnik) {
        this.loggedInUser = korisnik;

        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setTitle("Laboratorije");
        stage.setMinWidth(500);
        stage.setMinHeight(420);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.setTop(buildTopBar());
        root.setCenter(buildListaPanel());

        stage.setScene(new Scene(root, 560, 480));
        stage.show();
    }

    // ─── TOP BAR — naslov + korisnik ─────────────────────────────
    private HBox buildTopBar() {
        Label title = new Label("Laboratorije");
        title.setFont(Font.font("System", FontWeight.BOLD, 15));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLbl = new Label("Korisnik: " + loggedInUser);
        userLbl.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px;");

        HBox bar = new HBox(12, title, spacer, userLbl);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return bar;
    }

    // ─── LISTA — zauzima ceo centar ───────────────────────────────
    private VBox buildListaPanel() {
        listaView.setCellFactory(lv -> new LaboratorijaCellFactory());
        listaView.setStyle("-fx-font-size: 13px;");
        VBox.setVgrow(listaView, Priority.ALWAYS);

        VBox panel = new VBox(listaView);
        panel.setPadding(new Insets(16));
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }



    // ─────────────────────────────────────────────────────────────
    // INNER CLASS — custom ListCell
    // Svaki red: naziv  [izmeni] [obriši]
    // ─────────────────────────────────────────────────────────────
    public class LaboratorijaCellFactory extends ListCell<LaboratorijaDTO> {

        private final HBox   cellBox    = new HBox(10);
        private final Label  nazivLabel = new Label();
        private final Button izmeniBtn  = new Button();
        private final Button obrisiBtn  = new Button();

        public LaboratorijaCellFactory() {
            nazivLabel.setStyle("-fx-font-size: 13px;");

            izmeniBtn.setGraphic(loadIcon("labIzmeni.png", 15));
            obrisiBtn.setGraphic(loadIcon("labDelete.png", 15));
            izmeniBtn.setTooltip(new Tooltip("Izmeni"));
            obrisiBtn.setTooltip(new Tooltip("Obriši"));
            izmeniBtn.setStyle(ikonaBtnStyle());
            obrisiBtn.setStyle(ikonaBtnStyle());

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            cellBox.setAlignment(Pos.CENTER_LEFT);
            cellBox.setPadding(new Insets(5, 8, 5, 8));
            cellBox.getChildren().addAll(nazivLabel, spacer, izmeniBtn, obrisiBtn);

            izmeniBtn.setOnAction(e -> {
                if (getItem() != null && izmeniCallback != null)
                    izmeniCallback.accept(getItem());
            });

            obrisiBtn.setOnAction(e -> {
                if (getItem() != null && obrisiCallback != null)
                    obrisiCallback.accept(getItem());
            });
        }

        @Override
        protected void updateItem(LaboratorijaDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                nazivLabel.setText(item.getNaziv());
                setGraphic(cellBox);
                setText(null);
            }
        }

        private String ikonaBtnStyle() {
            return  "-fx-background-color: transparent;" +
                    "-fx-border-color: #d0d0d0;" +
                    "-fx-border-radius: 5;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 3 7 3 7;";
        }

        private ImageView loadIcon(String name, int size) {
            try {
                Image img = new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream("/" + name)
                        )
                );
                ImageView iv = new ImageView(img);
                iv.setFitWidth(size);
                iv.setFitHeight(size);
                iv.setPreserveRatio(true);
                return iv;
            } catch (Exception ex) {
                return new ImageView();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // POMOĆNE METODE
    // ─────────────────────────────────────────────────────────────
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

    private void styleField(TextField tf) {
        tf.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5 8 5 8;"
        );
    }

    // ─────────────────────────────────────────────────────────────
    // CALLBACK SETTERI
    // ─────────────────────────────────────────────────────────────
    public void setIzmeniCallback(Consumer<LaboratorijaDTO> cb) { this.izmeniCallback = cb; }
    public void setObrisiCallback(Consumer<LaboratorijaDTO> cb) { this.obrisiCallback = cb; }

    // ─────────────────────────────────────────────────────────────
    // GETTERI za controller
    // ─────────────────────────────────────────────────────────────
    public ListView<LaboratorijaDTO> getListaView()  { return listaView; }
    public Stage     getStage()                      { return stage; }
}