package org.baze.neuronauka;

import javafx.application.Application;
import javafx.stage.Stage;
import org.baze.neuronauka.view.LogSign;
import static javafx.application.Application.launch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LogSign view = new LogSign(primaryStage);
        view.show();
    }
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        launch(args);
    }
}