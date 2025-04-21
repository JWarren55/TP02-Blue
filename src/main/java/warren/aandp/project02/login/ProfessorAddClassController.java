package warren.aandp.project02.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class ProfessorAddClassController {


    public ChoiceBox AMorPMChoiceBox;
    public Label AddNewClassLabel;
    private Stage stage;
    private Scene scene;
    public TextField courseNameField;
    public TextField timesField;
    public CheckBox mondayBox;
    public CheckBox tuesdayBox;
    public CheckBox wednesdayBox;
    public CheckBox thursdayBox;
    public CheckBox fridayBox;

    public String professorID;
    ManagmentMethods mm = new ManagmentMethods();
    AddClassMethods adm = new AddClassMethods();

    public void setID(String userID) { professorID = userID; }

    public void onAddClassButtonClass(ActionEvent actionEvent) throws IOException {
        String courseName  = courseNameField.getText();
        String courseTime  = timesField.getText();
        boolean monday     = mondayBox.isSelected();
        boolean tuesday    = tuesdayBox.isSelected();
        boolean wednesday  = wednesdayBox.isSelected();
        boolean thursday   = thursdayBox.isSelected();
        boolean friday     = fridayBox.isSelected();
        String  AMorPM     = AMorPMChoiceBox.getValue().toString();

        String status = adm.addClass(
                professorID, courseName, courseTime, AMorPM,
                monday, tuesday, wednesday, thursday, friday
        );
        AddNewClassLabel.setText(status);

        if (status.equals("Class Added")) {
            refreshProfessorHomeScreen(actionEvent);
        }
    }


    private void refreshProfessorHomeScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("ProfessorHome.fxml"));
        Parent root = fx.load();
        ProfessorHomeControler controller = fx.getController();

        controller.setID(professorID);
        controller.professorHomeLabel.setText("Your class \"" + courseNameField.getText() + "\" has been added");

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Hello " + mm.findUserName(professorID));
        stage.setScene(new Scene(root, 640, 420));
        stage.show();
    }

    public void onGoBackButtonClicked(ActionEvent actionEvent) throws IOException {
        //Load CLass detail Screen
        FXMLLoader fxmlLoaderProfessorHome = new FXMLLoader(MainApplication.class.getResource("ProfessorHome.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderProfessorHome.load(), 640, 420);
        stage.setTitle("Hello " + mm.findUserName(professorID));
        stage.setScene(scene);
        //pass Professor ID
        ProfessorHomeControler controller = fxmlLoaderProfessorHome.getController();
        controller.setID(professorID);

        stage.show();
    }

}
