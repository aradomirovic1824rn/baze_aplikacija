package org.baze.neuronauka;

import javafx.application.Application;
import javafx.stage.Stage;
import org.baze.neuronauka.controller.AuthController;
import org.baze.neuronauka.db.DBConnection;
import org.baze.neuronauka.view.LogInView;
import org.baze.neuronauka.view.SignUpView;

import java.sql.Connection;

import static javafx.application.Application.launch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage stage){
        LogInView view = new LogInView();

        AuthController controller = new AuthController(view); // 👈 OBAVEZNO

        view.show(stage);
    }
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("✔ Konekcija uspešna!");
        } else {
            System.out.println("✖ Konekcija nije uspešna!");
        }
        launch(args);

    }
}