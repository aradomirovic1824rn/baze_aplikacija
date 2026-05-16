package org.baze.neuronauka.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.baze.neuronauka.controller.AuthController;

public class LogInView {
    private TextField username = new TextField();
    private PasswordField password = new PasswordField();

    private Button loginBtn = new Button("Login");
    private Button signUpBtn = new Button("Sign up");

    private Label message = new Label();

    public void show(Stage stage){

        username.setPromptText("Username");
        password.setPromptText("Password");

        VBox root = new VBox(10,
                username,
                password,
                loginBtn,
                signUpBtn,
                message
        );

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Login");
        stage.show();
    }

    // 👇 GETTERI (controller koristi view)
    public TextField getUsername() { return username; }
    public PasswordField getPassword() { return password; }

    public Button getLoginBtn() { return loginBtn; }
    public Button getSignUpBtn() { return signUpBtn; }

    public Label getMessage() { return message; }

}
