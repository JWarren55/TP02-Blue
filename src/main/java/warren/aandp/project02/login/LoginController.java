package warren.aandp.project02.login;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.EventObject;

public class LoginController {
    public Label LoginText;
    private Stage stage;
    private Scene scene;
    public TextField emailField;
    public PasswordField passwordField;

    LoginMethods loginMethods = new LoginMethods();
    ManagmentMethods managmantMathods = new ManagmentMethods();
    StudentHomeController studentHomeMethods = new StudentHomeController();


    public void login(String profession, ActionEvent actionEvent) throws IOException {

        String nextScreen;
        if(profession.equals("Student")) {
            nextScreen = "StudentHome.fxml"; // make the Student screen
        } else {
            nextScreen = "ProfessorHome.fxml"; // make the professor screen
        }
         
        //get email and password from text box
        String email = emailField.getText();
        String password = passwordField.getText();

        String userID = loginMethods.loginUser(profession, email, password);

        //look for user and check password
        String userName = managmantMathods.findUserName(userID);
        //set text at top of screen for name (used in case screen does not load and bad things are happening
        LoginText.setText(userName);

        if(!userID.equals("Wrong Password") && !userID.equals("Login Failed") && !userID.equals("no id found")) {

            // Load the FXML file
            FXMLLoader fxmlLoaderUser = new FXMLLoader(getClass().getResource(nextScreen));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoaderUser.load(), 640, 420);
            stage.setTitle("Hello " + userName);
            stage.setScene(scene);

            if (nextScreen.equals("StudentHome.fxml")) {
                // Gives next screen ID
                StudentHomeController controller = fxmlLoaderUser.getController();
                controller.setID(userID);

            } else {
                ProfessorHomeControler controller = fxmlLoaderUser.getController();
                controller.setID(userID);
            }

           
            stage.show();


        }
    }

    public void onLoginStudentButtonClick(ActionEvent actionEvent) throws IOException {

        login("Student", actionEvent);

    }

    public void onLoginProfessorButtonClick(ActionEvent actionEvent) throws IOException {

        login("Professor", actionEvent);

    }

    //On cancel button clicked
    public void onCancelButtonClick(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoaderStart = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderStart.load(), 640, 420);
        stage.setTitle("Sign Up or Login");
        stage.setScene(scene);
        stage.show();

    }

}
