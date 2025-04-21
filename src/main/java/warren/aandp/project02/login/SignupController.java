package warren.aandp.project02.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignupController {
    //get methods
    LoginMethods m = new LoginMethods();
    //Sign Up Label
    public Label SignupText;
    //fields for email, name and password to make account
    public TextField emailField;
    public TextField nameField;
    public PasswordField passwordField;

    private Stage stage;
    private Scene scene;

    public void signUp(String profession, ActionEvent actionEvent ) throws NoSuchPaddingException, IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {


        //Get email, name, and password
        String email = emailField.getText();
        String name = nameField.getText();
        String password = passwordField.getText();

        //Add user to User.txt
        String signUpStatus = m.addUser(profession, email, name, password);

        if(signUpStatus.equals("email in use")) {
            SignupText.setText("Email is in use");
        } else {
            //go back to start screen
            FXMLLoader fxmlLoaderStart = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoaderStart.load(), 640, 420);
            stage.setTitle("Sign Up");
            stage.setScene(scene);
            //set welcom text on start screen to show the user added an account
            MainController controller = fxmlLoaderStart.getController();
            controller.setWelcomText();
            stage.show();
        }
    }

    //On Sign up button Clicked
    public void onSignUpStudentButtonClick(ActionEvent actionEvent) throws NoSuchPaddingException, IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        signUp("Student", actionEvent);

    }

    public void onSignUpProfessorButtonClick(ActionEvent actionEvent) throws NoSuchPaddingException, IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        signUp("Professor", actionEvent);

    }

    //When Cancel Button is clicked
    public void onCancelButtonClick(ActionEvent actionEvent) throws IOException {

        //Go to start Menu
        FXMLLoader fxmlLoaderStart = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderStart.load(), 640, 420);
        stage.setTitle("Sign Up or Login");
        stage.setScene(scene);
        stage.show();

    }
}
