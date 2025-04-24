package warren.aandp.project02.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProfessorClassDetailControler {

    @FXML public Label classDetailLabel;                       // shows course info
    @FXML public TableView<ProfessorClassDetailsInfo> enrolledStudentsTable;
    @FXML public TableColumn<ProfessorClassDetailsInfo,String> colID, colName, colEmail, colGrade;
    @FXML private TextField upGradeTextfield;                  // input for grade update

    private Stage stage;
    private Scene scene;
    ManagmentMethods mm = new ManagmentMethods();             // helper for user data
    String professorID, courseID;

    // store professor ID
    public void setID(String userID) { professorID = userID; }

    // set course context and populate
    public void setCourseID(String courseID) {
        this.courseID = courseID;
        classDetailLabel.setText("Class Detail for " + courseID);
        populateScreen();
    }

    public void onSaveGradeButtonClick(ActionEvent actionEvent) {
        // placeholder
    }

    // update grade for selected student
    public void updateGradeButtonClick(ActionEvent actionEvent) throws IOException {
        AppendingMethods am = new AppendingMethods();
        ProfessorClassDetailsInfo sel = enrolledStudentsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String newGrade = upGradeTextfield.getText().trim();
        am.updateGrade(sel.getStudentID(), courseID, newGrade);   // write new grade
        populateScreen();                                         // refresh table
    }

    // go back to professor home
    public void onGoBackButtonClick(ActionEvent actionEvent) throws IOException {
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

    // configure table columns
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    // load enrolled students from Course.txt and Student.txt
    private void populateScreen() {
        if (courseID == null) return;
        String[] courseLine = findCourseLine(courseID);
        if (courseLine == null || courseLine.length < 6) return;

        String courseName = courseLine[1].trim();
        classDetailLabel.setText("Class detail – " + courseName);

        List<String> sids = new ArrayList<>();
        for (int i = 5; i < courseLine.length; i++) sids.add(courseLine[i].trim());

        ObservableList<ProfessorClassDetailsInfo> rows = FXCollections.observableArrayList();
        for (String sid : sids) {
            String[] stu = findStudentLine(sid);
            if (stu == null || stu.length < 5) continue;
            rows.add(new ProfessorClassDetailsInfo(
                    sid, stu[2].trim(), stu[1].trim(), findGradeForCourse(stu, courseID)
            ));
        }
        enrolledStudentsTable.setItems(rows);
    }

    // helpers to read file lines
    private String[] findCourseLine(String cid) {
        try (BufferedReader br = open("/warren/aandp/project02/login/Course.txt")) {
            String l;
            while (br != null && (l = br.readLine()) != null) {
                if (l.trim().startsWith(cid + ",")) return l.trim().split(",");
            }
        } catch (IOException ignored) {}
        return null;
    }
    private String[] findStudentLine(String sid) {
        try (BufferedReader br = open("/warren/aandp/project02/login/Student.txt")) {
            String l;
            while (br != null && (l = br.readLine()) != null) {
                if (l.trim().startsWith(sid + ",")) return l.trim().split(",");
            }
        } catch (IOException ignored) {}
        return null;
    }
        private String findGradeForCourse(String[] parts, String cid) {
        for (int i = 5; i < parts.length-1; i+=2) {
            if (parts[i+1].trim().equals(cid)) return parts[i].trim();
        }
        return "–";
    }
    private BufferedReader open(String path) {
        InputStream in = getClass().getResourceAsStream(path);
        return (in==null)?null:new BufferedReader(new InputStreamReader(in));
    }
}
