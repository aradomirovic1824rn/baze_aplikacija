package org.baze.neuronauka.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.baze.neuronauka.controller.AuthController;

public class SignUpView {
    private TextField username = new TextField();
    private PasswordField password = new PasswordField();

    private Button registerBtn = new Button("Register");
    private Button backBtn = new Button("Back");

    private Label message = new Label();

    public void show(Stage stage){

        username.setPromptText("Username");
        password.setPromptText("Password");

        VBox root = new VBox(10,
                username,
                password,
                registerBtn,
                backBtn,
                message
        );

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Sign Up");
        stage.show();
    }

    // GETTERI za controller
    public TextField getUsername() { return username; }
    public PasswordField getPassword() { return password; }

    public Button getRegisterBtn() { return registerBtn; }
    public Button getBackBtn() { return backBtn; }

    public Label getMessage() { return message; }
}
