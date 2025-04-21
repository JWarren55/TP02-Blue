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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProfessorHomeControler {

    @FXML public Label professorHomeLabel;
    @FXML private TableView<ProfessorHomeInfo> professorClassesTable;
    @FXML private TableColumn<ProfessorHomeInfo, String> colCourseID;
    @FXML private TableColumn<ProfessorHomeInfo, String> colClassName;
    @FXML private TableColumn<ProfessorHomeInfo, String> ColTime;
    @FXML private TableColumn<ProfessorHomeInfo, String> ColDays;
    @FXML public Stage stage;
    @FXML public Scene scene;

    public String professorID;

    // Called once after FXML loads:
    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        ColTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        ColDays.setCellValueFactory(new PropertyValueFactory<>("days"));
    }

   public void setID(String userID) throws IOException {
       this.professorID = userID;
       populateAllCourses();
   }

    public void onClassDetailButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderClassDetail = new FXMLLoader(MainApplication.class.getResource("ProfessorClassDetail.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderClassDetail.load(), 640, 420);
        stage.setTitle("Class Detail");
        stage.setScene(scene);

        // pass Professor ID
        ProfessorClassDetailControler controller = fxmlLoaderClassDetail.getController();
        controller.setID(professorID);
        //pass CourseID
        try {
            ProfessorHomeInfo selectedCourse = professorClassesTable.getSelectionModel().getSelectedItem();
            String courseID = selectedCourse.getCourseID();
            controller.setCourseID(courseID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        stage.show();
    }

    public void onAddClassButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderAddClass = new FXMLLoader(MainApplication.class.getResource("ProfessorAddClass.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderAddClass.load(), 640, 420);
        stage.setTitle("Add a class");
        stage.setScene(scene);

        // pass Professor ID
        ProfessorAddClassController controller = fxmlLoaderAddClass.getController();
        controller.setID(professorID);

        stage.show();
    }

    private void populateAllCourses() throws IOException {
        String[] professorLine = findProfessorLine(professorID);
        if (professorLine == null || professorLine.length < 6) {
            return;
        }

        Set<String> professorCourseIDs = new HashSet<>();
        for (int i = 5; i < professorLine.length; i++) {
            professorCourseIDs.add(professorLine[i].trim());
        }

        ObservableList<ProfessorHomeInfo> professorClasses = FXCollections.observableArrayList();

        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            if (br == null) {
                System.out.println("Could not find Course.txt resource!");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] course = line.split(",");
               if (course.length >= 5) {
                    String courseID   = course[0].trim();
                    String courseName = course[1].trim();
                    String courseTime = course[2].trim();
                    String courseDays = course[3].trim();

                    if (professorCourseIDs.contains(courseID)) {
                        ProfessorHomeInfo row = new ProfessorHomeInfo(courseID, courseName, courseTime, courseDays);
                        professorClasses.add(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        professorClassesTable.setItems(professorClasses);
    }

    private String[] findProfessorLine(String professorID) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/professor.txt")) {
            if (br == null) {
                System.out.println("Could not find professor.txt resource!");
                return null;
            }
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] course = line.split(",");
                if (course.length > 0) {
                    String fileProfessorID = course[0].trim();
                    if (fileProfessorID.equals(professorID)) {
                        return course;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public void onLogoutButtonClicked(ActionEvent actionEvent) {

    }
}
