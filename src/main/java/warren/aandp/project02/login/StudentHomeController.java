/* ================================
   StudentHomeController.java
   ================================ */
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StudentHomeController {

    AppendingMethods appendingMethods = new AppendingMethods();

    private Stage stage;
    private Scene scene;

    private String studentID;

    @FXML private TableView<StudentCourseInfo> tblCourses;
    @FXML private TableColumn<StudentCourseInfo, String> colCourseID;
    @FXML private TableColumn<StudentCourseInfo, String> colCourseName;
    @FXML private TableColumn<StudentCourseInfo, String> colProfessor;
    @FXML private TableColumn<StudentCourseInfo, String> colTime;
    @FXML private TableColumn<StudentCourseInfo, String> colDays;
    @FXML private TableColumn<StudentCourseInfo, String> colGrade;
    @FXML private Label gpaLbl;

    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colProfessor.setCellValueFactory(new PropertyValueFactory<>("professorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("days"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    public void setID(String userID) {
        this.studentID = userID;
        populateTableForStudent();
    }

    public void onEnrollInClassButtonClicked(ActionEvent a) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("StudentEnroll.fxml"));
        stage = (Stage) ((Node) a.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fx.load(), 640, 420));
        fx.<StudentEnrollControler>getController().setID(studentID);
        stage.show();
    }

    public void onCourseRowClicked(MouseEvent e) {}

    public void onUnenrollButtonClicked(ActionEvent actionEvent) throws IOException {
        StudentCourseInfo selectedCourse = tblCourses.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            //String courseName = selectedCourse.getCourseName();
            String courseID = selectedCourse.getCourseID();
            appendingMethods.deleteFromLine(studentID, courseID);
            appendingMethods.deleteFromLine(courseID,studentID);
        }
    }

    public void populateTableForStudent() {
        String[] parts = findStudentLine(studentID);
        ObservableList<StudentCourseInfo> data = FXCollections.observableArrayList();
        if (parts == null) return;

        /* ---------- added: robust scan that pairs any grade‑like token with the course token right after it ---------- */
        for (int i = 5; i < parts.length; i++) {                               // ← added
            String tok = parts[i].trim();                                      // ← added
            if (tok.startsWith("C-")) {                                        // ← added
                String gradeTok = (i > 0) ? parts[i - 1].trim() : "N/A";       // ← added
                String[] c = findCourseLine(tok);                              // ← added
                if (c != null && c.length >= 5) {                              // ← added
                    data.add(new StudentCourseInfo(                            // ← added
                            c[0].trim(), c[1].trim(),                          // ← added
                            findProfessorName(c[4].trim()),                    // ← added
                            c[2].trim(), c[3].trim(), gradeTok));              // ← added
                }                                                              // ← added
            }                                                                  // ← added
        }                                                                      // ← added

        tblCourses.setItems(data);                                             // ← changed (same call, but now after new loop)
        tblCourses.refresh();                                                  // ← added
        updateGPA();
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

    private String[] findCourseLine(String cid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            if (br == null) return null;
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length > 0 && p[0].trim().equals(cid)) return p;
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


    private void updateGPA() {
        ObservableList<StudentCourseInfo> rows = tblCourses.getItems();
        if (rows == null || rows.isEmpty()) { gpaLbl.setText("N/A"); return; }
        double sum = 0; int n = 0;
        for (StudentCourseInfo r : rows) {
            double pts = percentToPts(r.getGrade());
            if (pts >= 0) { sum += pts; n++; }                                 // ← changed (ignore non‑numeric)
        }
        gpaLbl.setText(n == 0 ? "N/A" : String.format("Overall GPA: %.2f", sum / n));
    }

    private double percentToPts(String g) {
        if (g == null || g.isBlank()) return -1;                               // ← changed
        g = g.replace("%", "").trim();
        int v;
        try { v = Integer.parseInt(g); } catch (NumberFormatException e) { return -1; } // ← changed
        if (v >= 90) return 4;
        if (v >= 80) return 3;
        if (v >= 70) return 2;
        if (v >= 60) return 1;
        return 0;
    }

    public void onLogoutButtonClicked(ActionEvent e) {}

}
