package warren.aandp.project02.login;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    @FXML
    public Label classDetailLabel;
    @FXML
    public TableView<ProfessorClassDetailsInfo> enrolledStudentsTable;
    @FXML
    public TableColumn<ProfessorClassDetailsInfo, String> colID;
    @FXML
    public TableColumn<ProfessorClassDetailsInfo, String> colName;
    @FXML
    public TableColumn<ProfessorClassDetailsInfo, String> colEmail;
    @FXML
    public TableColumn<ProfessorClassDetailsInfo, String> colGrade;
    private Stage stage;
    private Scene scene;

    ManagmentMethods mm = new ManagmentMethods();
    String professorID;
    String courseID;

    public void setID(String userID) {
        professorID = userID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
        classDetailLabel.setText("Class Detail for " + courseID);
        populateScreen();
    }

    public void onSaveGradeButtonClick(ActionEvent actionEvent) {
    }

    public void onGoBackButtonClick(ActionEvent actionEvent) throws IOException {

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

    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    private void populateScreen() {
        if (courseID == null) return;

        String[] courseLine = findCourseLine(courseID);
        if (courseLine == null || courseLine.length < 6) return;

        String courseName   = courseLine[1].trim();
        List<String> studentIDs = new ArrayList<>();
        for (int i = 5; i < courseLine.length; i++) {
            studentIDs.add(courseLine[i].trim());
        }

        classDetailLabel.setText("Class detail – " + courseName);

        ObservableList<ProfessorClassDetailsInfo> rows = FXCollections.observableArrayList();
        for (String sid : studentIDs) {
            String[] stu = findStudentLine(sid);
            if (stu == null || stu.length < 5) continue;

            String email = stu[1].trim();
            String name  = stu[2].trim();
            String grade = findGradeForCourse(stu, courseID);

            rows.add(new ProfessorClassDetailsInfo(sid, name, email, grade));
        }

        enrolledStudentsTable.setItems(rows);
    }

    private String[] findCourseLine(String cid) {
        try (BufferedReader br = open("/warren/aandp/project02/login/Course.txt")) {
            if (br == null) return null;
            String l;
            while ((l = br.readLine()) != null) {
                l = l.trim();
                if (l.startsWith(cid + ",")) return l.split(",");
            }
        } catch (IOException ignored) {}
        return null;
    }

    private String[] findStudentLine(String sid) {
        try (BufferedReader br = open("/warren/aandp/project02/login/Student.txt")) {
            if (br == null) return null;
            String l;
            while ((l = br.readLine()) != null) {
                l = l.trim();
                if (l.startsWith(sid + ",")) return l.split(",");
            }
        } catch (IOException ignored) {}
        return null;
    }

    private String findGradeForCourse(String[] stuParts, String cid) {
        for (int i = 5; i < stuParts.length - 1; i += 2) {
            String grade = stuParts[i].trim();
            String course = stuParts[i + 1].trim();
            if (course.equals(cid)) return grade;
        }
        return "–";
    }

    private BufferedReader open(String path) {
        InputStream in = getClass().getResourceAsStream(path);
        return (in == null) ? null : new BufferedReader(new InputStreamReader(in));
    }

    @FXML
    private TextField upGradeTextfield;
    public void updateGradeButtonClick (ActionEvent actionEvent) throws IOException{
        AppendingMethods am = new AppendingMethods();
        ProfessorClassDetailsInfo selectedStudent = (ProfessorClassDetailsInfo) enrolledStudentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            System.out.println("Please select a student");
            return;
        } 
        String studentID = selectedStudent.getStudentID();
        String courseID = this.courseID;
    
        String newGrade = upGradeTextfield.getText().trim(); // Define and assign newGrade
        
        if(newGrade.isEmpty()) {
            System.out.println("Please enter a new grade");
            return;
        }
        am.updateGrade(studentID, courseID, newGrade);   
        
        ObservableList<ProfessorClassDetailsInfo> updatedList = getUpdatedStudentList();
        enrolledStudentsTable.setItems(updatedList);
        

    }
    private List<String> getStudentIDsFromCourse(String courseID) {
        List<String> studentIDs = new ArrayList<>();
    
        String[] courseLine = findCourseLine(courseID);
        if (courseLine == null || courseLine.length < 6) return studentIDs; // Ensure valid data
    
        // Extract student IDs starting from index 5 (after course details)
        for (int i = 5; i < courseLine.length; i++) {
            studentIDs.add(courseLine[i].trim());
        }
    
        return studentIDs;
    }
    public void refreshProfessorTable() {
        populateScreen();
        enrolledStudentsTable.refresh(); // Refresh the table to show the updated data

    }
    private ObservableList<ProfessorClassDetailsInfo> getUpdatedStudentList() {
        ObservableList<ProfessorClassDetailsInfo> rows = FXCollections.observableArrayList();
        String[] courseLine = findCourseLine(courseID);
        if (courseLine == null || courseLine.length < 6) return rows;

        List<String> studentIDs = getStudentIDsFromCourse(courseID);

        for (String sid : studentIDs) {
            String[] stu = findStudentLine(sid);
            if (stu == null || stu.length < 5) continue;
    
            String email = stu[1].trim();
            String name  = stu[2].trim();
            String grade = findGradeForCourse(stu, courseID);
    
            rows.add(new ProfessorClassDetailsInfo(sid, name, email, grade));
        }
        return rows;
    }

    
    
   
}
