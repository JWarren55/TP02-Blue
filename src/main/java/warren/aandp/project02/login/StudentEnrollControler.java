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
    ManagmentMethods mm = new ManagmentMethods();

    @FXML private TableView<StudentEnrollInfo> tblAllCourses;
    @FXML private TableColumn<StudentEnrollInfo, String> colCourseID;
    @FXML private TableColumn<StudentEnrollInfo, String> colCourseName;
    @FXML private TableColumn<StudentEnrollInfo, String> colProfessor;
    @FXML private TableColumn<StudentEnrollInfo, String> colTime;
    @FXML private TableColumn<StudentEnrollInfo, String> colDays;

    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colProfessor.setCellValueFactory(new PropertyValueFactory<>("professorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("days"));
    }

    public void setID(String userID) {
        this.studentID = userID;
        populateAllCourses();
    }

    public void onEnrollButtonClicked(ActionEvent e) throws IOException {
        AppendingMethods am = new AppendingMethods();
        StudentEnrollInfo sel = tblAllCourses.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        String courseID = sel.getCourseID();
        am.appendToLine(studentID, courseID);     // let AppendingMethods add 100% + comma
        am.appendToLine(courseID,  studentID);    // course roster

        refreshStudentHomeScreen(e);
    }


    private void refreshStudentHomeScreen(ActionEvent actionEvent) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("StudentHome.fxml"));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fx.load(), 640, 420);
        stage.setTitle("Hello " + mm.findUserName(studentID));
        stage.setScene(scene);
        fx.<StudentHomeController>getController().setID(studentID);
        stage.show();
    }

    private void populateAllCourses() {
        Set<String> enrolled = findEnrolledCourses(studentID);
        ObservableList<StudentEnrollInfo> data = FXCollections.observableArrayList();
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            if (br == null) return;
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.trim().split(",");
                if (p.length >= 5) {
                    String cid = p[0].trim();
                    if (enrolled.contains(cid)) continue;
                    data.add(new StudentEnrollInfo(
                            cid, p[1].trim(), findProfessorName(p[4].trim()),
                            p[2].trim(), p[3].trim()));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        tblAllCourses.setItems(data);
        tblAllCourses.refresh();
    }

    private Set<String> findEnrolledCourses(String id) {
        Set<String> s = new HashSet<>();
        String[] parts = findStudentLine(id);
        if (parts == null) return s;
        for (int i = 5; i < parts.length; i++) {       // ← changed (step = 1 to survive mis‑shifts)
            String tok = parts[i].trim();
            if (tok.startsWith("C-")) s.add(tok);
        }
        return s;
    }

    private String[] findStudentLine(String id) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Student.txt")) {
            if (br == null) return null;
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length > 0 && p[0].trim().equals(id)) return p;
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    private String findProfessorName(String pid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Professor.txt")) {
            if (br == null) return "Unknown";
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length >= 3 && p[0].trim().equals(pid)) return p[2].trim();
            }
        } catch (IOException e) { e.printStackTrace(); }
        return "Unknown";
    }

    // Reusable method for opening a resource from src/main/resources

    private BufferedReader openResource(String resourcePath) {
        try {
            // try the file we just modified during this session
            Path path = Paths.get("src/main/resources").resolve(resourcePath.substring(1));
            if (Files.exists(path)) {
                return Files.newBufferedReader(path);   // <-- live data
            }

            // fall back to the immutable class‑path copy (works in the packaged JAR)
            InputStream in = getClass().getResourceAsStream(resourcePath);
            return (in != null) ? new BufferedReader(new InputStreamReader(in)) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onGoBackButtonClick(ActionEvent a) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("StudentHome.fxml"));
        stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
        scene = new Scene(fx.load(), 640, 420);
        stage.setScene(scene);
        fx.<StudentHomeController>getController().setID(studentID);
        stage.show();
    }
}
