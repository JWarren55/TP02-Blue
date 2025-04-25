/* ================================
   StudentEnrollControler.java
   ================================ */
package warren.aandp.project02.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class StudentEnrollControler {

    private Stage stage;
    private Scene scene;
    private String studentID;
    ManagmentMethods mm = new ManagmentMethods();             // helper for user data

    @FXML private TableView<StudentEnrollInfo> tblAllCourses;
    @FXML private TableColumn<StudentEnrollInfo,String> colCourseID, colCourseName, colProfessor, colTime, colDays;

    // set up table columns
    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colProfessor.setCellValueFactory(new PropertyValueFactory<>("professorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("days"));
    }

    // receive student ID and list available courses
    public void setID(String userID) {
        this.studentID = userID;
        populateAllCourses();
    }

    // enroll in selected course
    public void onEnrollButtonClicked(ActionEvent e) throws IOException {
        AppendingMethods am = new AppendingMethods();
        StudentEnrollInfo sel = tblAllCourses.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        String cid = sel.getCourseID();
        am.appendToLine(studentID, cid);    // record in student
        am.appendToLine(cid, studentID);    // record in course roster
        refreshStudentHomeScreen(e);
    }

    // go back to home after enroll
    private void refreshStudentHomeScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader fx = new FXMLLoader(
                MainApplication.class.getResource("StudentHome.fxml")
        );
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fx.load(), 640, 420);
        stage.setTitle("Hello " + mm.findUserName(studentID));
        stage.setScene(scene);
        fx.<StudentHomeController>getController().setID(studentID);
        stage.show();
    }

    // list courses not yet enrolled
    private void populateAllCourses() {
        Set<String> enrolled = findEnrolledCourses(studentID);
        ObservableList<StudentEnrollInfo> data = FXCollections.observableArrayList();
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            String line;
            while (br!=null && (line=br.readLine())!=null) {
                String[] p = line.trim().split(",");
                if (p.length>=5 && !enrolled.contains(p[0].trim())) {
                    data.add(new StudentEnrollInfo(
                            p[0].trim(), p[1].trim(), findProfessorName(p[4].trim()),
                            p[2].trim(), p[3].trim()
                    ));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        tblAllCourses.setItems(data);
        tblAllCourses.refresh();
    }

    // get already enrolled course IDs from student record
    private Set<String> findEnrolledCourses(String id) {
        Set<String> s = new HashSet<>();
        String[] parts = findStudentLine(id);
        if (parts==null) return s;
        for (int i=5; i<parts.length; i++) if (parts[i].trim().startsWith("C-")) s.add(parts[i].trim());
        return s;
    }

    private String[] findStudentLine(String id) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Student.txt")) {
            String ln;
            while (br!=null && (ln=br.readLine())!=null) {
                String[] p = ln.trim().split(",");
                if (p[0].trim().equals(id)) return p;
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    private String findProfessorName(String pid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Professor.txt")) {
            String ln;
            while (br!=null && (ln=br.readLine())!=null) {
                String[] p = ln.trim().split(",");
                if (p[0].trim().equals(pid) && p.length>=3) return p[2].trim();
            }
        } catch (IOException e) { e.printStackTrace(); }
        return "Unknown";
    }

    // open resource from project or classpath
    private BufferedReader openResource(String resourcePath) {
        try {
            Path path = Paths.get("src/main/resources").resolve(resourcePath.substring(1));
            if (Files.exists(path)) return Files.newBufferedReader(path);
            InputStream in = getClass().getResourceAsStream(resourcePath);
            return (in!=null)?new BufferedReader(new InputStreamReader(in)):null;
        } catch (IOException e) { e.printStackTrace(); return null; }
    }

    public void onGoBackButtonClick(ActionEvent a) throws IOException {
        FXMLLoader fx = new FXMLLoader(
                MainApplication.class.getResource("StudentHome.fxml")
        );
        stage = (Stage)((Node)a.getSource()).getScene().getWindow();
        scene = new Scene(fx.load(), 640, 420);
        fx.<StudentHomeController>getController().setID(studentID);
        stage.setScene(scene);
        stage.show();
    }
}
