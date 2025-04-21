package warren.aandp.project02.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    public Label welcomeText;
    private Stage stage;
    private Scene scene;

    //This is Noah
    // This is Lawrence
    //This is Symphony.

    //Sign in button clicked, go to Sign up screen
    @FXML
    protected void onSignUpButtonClick(javafx.event.ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoaderSignup = new FXMLLoader(MainApplication.class.getResource("signup-screen.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderSignup.load(), 640, 420);
        stage.setTitle("Sign Up");
        stage.setScene(scene);
        stage.show();

    }

    //Login button clicked, go to log in screen
    @FXML
    protected void onLogInButtonClick(javafx.event.ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoaderLogin = new FXMLLoader(MainApplication.class.getResource("login-screen.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderLogin.load(), 640, 420);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

    }

    //Change Welcome text after Sign in
    public void setWelcomText() {
        welcomeText.setText("New User Added");
    }
}