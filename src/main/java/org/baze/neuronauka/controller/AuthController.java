package org.baze.neuronauka.controller;
import javafx.stage.Stage;
import org.baze.neuronauka.service.AuthService;
import org.baze.neuronauka.view.LogInView;
import org.baze.neuronauka.view.SesijaView;
import org.baze.neuronauka.view.SignUpView;

public class AuthController {
    private AuthService service = new AuthService();
    private LogInView view;

    public AuthController(LogInView view){
        this.view = view;
        initActions();
    }

    public void attachSignUp(SignUpView view){

        view.getRegisterBtn().setOnAction(e -> {

            boolean ok = service.register(
                    view.getUsername().getText(),
                    view.getPassword().getText()
            );

            if(ok){
                view.getMessage().setText("Registered successfully");
            } else {
                view.getMessage().setText("User already exists");
            }
        });

        view.getBackBtn().setOnAction(e -> {

            LogInView loginView = new LogInView();
            AuthController controller = new AuthController(loginView);

            loginView.show((Stage) view.getBackBtn().getScene().getWindow());
        });
    }

    private void initActions(){

        view.getLoginBtn().setOnAction(e -> {
            String result = service.login(
                    view.getUsername().getText(),
                    view.getPassword().getText()
            );

            switch(result){
                case "LOGIN_OK":
                    SesijaView sesijaView = new SesijaView();

                    SesijaController sesijaCtrl = new SesijaController(
                                    sesijaView,
                                    view.getUsername().getText()
                            );

                    sesijaView.show(
                            (Stage) view.getLoginBtn().getScene().getWindow(),
                            view.getUsername().getText()
                    );
                    break;



                case "USER_NOT_FOUND":
                    view.getMessage().setText("Korisnik ne postoji, registrujte se");
                    break;

                default:
                    view.getMessage().setText("Pogrešan username ili password");
            }
        });

        view.getSignUpBtn().setOnAction(e -> {

            SignUpView signUpView = new SignUpView();
            attachSignUp(signUpView);

            signUpView.show((Stage) view.getSignUpBtn().getScene().getWindow());
        });
    }

}
