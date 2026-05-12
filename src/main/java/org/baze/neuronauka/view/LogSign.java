package org.baze.neuronauka.view;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LogSign {

    private Stage stage;

    public LogSign(Stage stage) {
        this.stage = stage;
    }
    public void show() {

        TextField username = new TextField();
        username.setPromptText("Username");

        TextField password = new TextField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Sign Up");

        VBox root = new VBox(10, username, password, loginBtn, signupBtn);

        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("Login / Sign Up");
        stage.setScene(scene);
        stage.show();

        // kasnije ovde dodaješ evente
        loginBtn.setOnAction(e -> {
            System.out.println("Login kliknut");
        });

        signupBtn.setOnAction(e -> {
            System.out.println("Sign up kliknut");
        });
    }
}

