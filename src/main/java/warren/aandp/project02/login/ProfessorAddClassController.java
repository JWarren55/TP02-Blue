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

    // UI elements for new class input
    public ChoiceBox AMorPMChoiceBox;
    public Label AddNewClassLabel;
    public TextField courseNameField, timesField;
    public CheckBox mondayBox, tuesdayBox, wednesdayBox, thursdayBox, fridayBox;

    private Stage stage;
    private Scene scene;
    public String professorID;

    ManagmentMethods mm = new ManagmentMethods();    // helper for user data
    AddClassMethods adm = new AddClassMethods();      // helper to add class

    // store logged-in professor ID
    public void setID(String userID) { professorID = userID; }

    // handle Add Class button click
    public void onAddClassButtonClass(ActionEvent actionEvent) throws IOException {
        String courseName = courseNameField.getText();         // get class name
        String courseTime = timesField.getText();              // get time
        boolean monday    = mondayBox.isSelected();            // days selected
        boolean tuesday   = tuesdayBox.isSelected();
        boolean wednesday = wednesdayBox.isSelected();
        boolean thursday  = thursdayBox.isSelected();
        boolean friday    = fridayBox.isSelected();
        String AMorPM     = AMorPMChoiceBox.getValue().toString(); // AM/PM

        // attempt to add class and show status
        String status = adm.addClass(
                professorID, courseName, courseTime, AMorPM,
                monday, tuesday, wednesday, thursday, friday
        );
        AddNewClassLabel.setText(status);

        if (status.equals("Class Added")) {
            refreshProfessorHomeScreen(actionEvent);           // go back on success
        }
    }

    // reload professor home view with updated list
    private void refreshProfessorHomeScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("ProfessorHome.fxml"));
        Parent root = fx.load();
        ProfessorHomeControler controller = fx.getController();

        controller.setID(professorID);                        // pass ID back
        controller.professorHomeLabel.setText(
                "Your class \"" + courseNameField.getText() + "\" has been added"
        );

        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.setTitle("Hello " + mm.findUserName(professorID));
        stage.setScene(new Scene(root, 640, 420));
        stage.show();
    }

    // handle Go Back button: load home without adding
    public void onGoBackButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("ProfessorHome.fxml")
        );
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(loader.load(), 640, 420);
        stage.setTitle("Hello " + mm.findUserName(professorID));
        stage.setScene(scene);
        ProfessorHomeControler controller = loader.getController();
        controller.setID(professorID);
        stage.show();
    }
}
