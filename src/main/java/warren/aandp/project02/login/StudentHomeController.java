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

    AppendingMethods appendingMethods = new AppendingMethods();    // helper for file updates

    private Stage stage;
    private Scene scene;
    private String studentID;                                     // current student

    @FXML private TableView<StudentCourseInfo> tblCourses;        // table of enrolled courses
    @FXML private TableColumn<StudentCourseInfo, String> colCourseID, colCourseName;
    @FXML private TableColumn<StudentCourseInfo, String> colProfessor, colTime, colDays, colGrade;
    @FXML private Label gpaLbl;                                  // displays computed GPA

    // configure columns to map to StudentCourseInfo properties
    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colProfessor.setCellValueFactory(new PropertyValueFactory<>("professorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("days"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }

    // receive logged-in student ID and populate their table
    public void setID(String userID) {
        this.studentID = userID;
        populateTableForStudent();
    }

    // navigate to enrollment screen
    public void onEnrollInClassButtonClicked(ActionEvent a) throws IOException {
        FXMLLoader fx = new FXMLLoader(MainApplication.class.getResource("StudentEnroll.fxml"));
        stage = (Stage)((Node)a.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fx.load(), 640, 420));
        fx.<StudentEnrollControler>getController().setID(studentID);
        stage.show();
    }

    // handle unenroll: remove from both student and course records
    public void onUnenrollButtonClicked(ActionEvent e) throws IOException {
        StudentCourseInfo sel = tblCourses.getSelectionModel().getSelectedItem();
        if (sel != null) {
            String cid = sel.getCourseID();
            appendingMethods.deleteFromLine(studentID, cid);
            appendingMethods.deleteFromLine(cid, studentID);
            populateTableForStudent();
        }
    }

    public void onCourseRowClicked(MouseEvent e) {
        // not used
    }

    // load enrolled courses + grades into table and update GPA
    private void populateTableForStudent() {
        String[] parts = findStudentLine(studentID);
        ObservableList<StudentCourseInfo> data = FXCollections.observableArrayList();
        if (parts == null) {
            tblCourses.setItems(data);
            gpaLbl.setText("N/A");
            return;
        }

        // scan tokens: grade token precedes course token "C-..."
        for (int i = 5; i < parts.length; i++) {
            String tok = parts[i].trim();
            if (tok.startsWith("C-")) {
                String gradeTok = (i > 0 ? parts[i-1].trim() : "N/A");
                String[] c = findCourseLine(tok);
                if (c != null && c.length >= 5) {
                    data.add(new StudentCourseInfo(
                            c[0].trim(), c[1].trim(),
                            findProfessorName(c[4].trim()),
                            c[2].trim(), c[3].trim(),
                            gradeTok
                    ));
                }
            }
        }

        tblCourses.setItems(data);
        tblCourses.refresh();
        updateGPA();
    }

    // read student.txt to find this student's line
    private String[] findStudentLine(String id) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Student.txt")) {
            String ln;
            while (br != null && (ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length > 0 && p[0].trim().equals(id)) return p;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // read Course.txt to find course details
    private String[] findCourseLine(String cid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            String ln;
            while (br != null && (ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length > 0 && p[0].trim().equals(cid)) return p;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // read Professor.txt to lookup professor name
    private String findProfessorName(String pid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Professor.txt")) {
            String ln;
            while (br != null && (ln = br.readLine()) != null) {
                String[] p = ln.trim().split(",");
                if (p.length >= 3 && p[0].trim().equals(pid)) return p[2].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    // open resource from src/main/resources or classpath fallback
    private BufferedReader openResource(String resourcePath) {
        try {
            Path path = Paths.get("src/main/resources").resolve(resourcePath.substring(1));
            if (Files.exists(path)) return Files.newBufferedReader(path);
            InputStream in = getClass().getResourceAsStream(resourcePath);
            return (in != null) ? new BufferedReader(new InputStreamReader(in)) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // compute GPA from percent grades and display
    private void updateGPA() {
        ObservableList<StudentCourseInfo> rows = tblCourses.getItems();
        if (rows == null || rows.isEmpty()) {
            gpaLbl.setText("N/A");
            return;
        }
        double sum = 0;
        int count = 0;
        for (StudentCourseInfo r : rows) {
            double pts = percentToPts(r.getGrade());
            if (pts >= 0) {
                sum += pts;
                count++;
            }
        }
        gpaLbl.setText(count == 0 ? "N/A" : String.format("Overall GPA: %.2f", sum / count));
    }

    // convert numeric percentage string to 4.0 scale
    private double percentToPts(String g) {
        if (g == null || g.isBlank()) return -1;
        g = g.replace("%", "").trim();
        int v;
        try {
            v = Integer.parseInt(g);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (v >= 90) return 4;
        if (v >= 80) return 3;
        if (v >= 70) return 2;
        if (v >= 60) return 1;
        return 0;
    }

    // logout back to start screen
    public void onLogoutButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderStart = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderStart.load(), 640, 420);
        stage.setTitle("Sign Up or Login");
        stage.setScene(scene);
        stage.show();
    }
}
