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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class ProfessorHomeControler {

    AppendingMethods appendingMethods = new AppendingMethods(); // helper for deletes

    @FXML public Label professorHomeLabel;
    @FXML private TableView<ProfessorHomeInfo> professorClassesTable;
    @FXML private TableColumn<ProfessorHomeInfo,String> colCourseID, colClassName, ColTime, ColDays;
    @FXML public Stage stage;
    @FXML public Scene scene;

    public String professorID;

    // set up columns
    public void initialize() {
        colCourseID.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        ColTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        ColDays.setCellValueFactory(new PropertyValueFactory<>("days"));
    }

    // load professor courses after login
    public void setID(String userID) throws IOException {
        this.professorID = userID;
        populateAllCourses();
    }

    // show class details for selected
    public void onClassDetailButtonClicked(ActionEvent actionEvent) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("ProfessorClassDetail.fxml")
        );
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(loader.load(), 640, 420);
        stage.setTitle("Class Detail");
        stage.setScene(scene);

        ProfessorClassDetailControler ctl = loader.getController();
        ctl.setID(professorID);
        String cid = professorClassesTable.getSelectionModel().getSelectedItem().getCourseID();
        ctl.setCourseID(cid);
        stage.show();
    }

    // navigate to Add Class view
    public void onAddClassButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("ProfessorAddClass.fxml")
        );
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(loader.load(), 640, 420);
        stage.setTitle("Add a class");
        stage.setScene(scene);

        ProfessorAddClassController ctl = loader.getController();
        ctl.setID(professorID);
        stage.show();
    }

    // delete selected class from all records
    public void onDeleteClassButtonClicked(ActionEvent actionEvent) throws IOException {
        ProfessorHomeInfo sel = professorClassesTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            String cid = sel.getCourseID();
            appendingMethods.deleteCourseFromStudents(cid);    // remove from students
            appendingMethods.deleteFromLine(professorID, cid); // remove from professor
            appendingMethods.deleteCourse(cid);                // remove course file entry
            populateAllCourses();                              // refresh list
        }
    }

    // read and show this prof’s courses
    private void populateAllCourses() throws IOException {
        String[] profLine = findProfessorLine(professorID);
        if (profLine==null || profLine.length<6) return;
        Set<String> cids = new HashSet<>();
        for (int i=5; i<profLine.length; i++) cids.add(profLine[i].trim());

        ObservableList<ProfessorHomeInfo> list = FXCollections.observableArrayList();
        try (BufferedReader br = openResource("/warren/aandp/project02/login/Course.txt")) {
            String ln;
            while (br!=null && (ln=br.readLine())!=null) {
                String[] p = ln.trim().split(",");
                if (p.length>=5 && cids.contains(p[0].trim())) {
                    list.add(new ProfessorHomeInfo(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim()));
                }
            }
        }
        professorClassesTable.setItems(list);
    }

    // find this prof in professor.txt
    private String[] findProfessorLine(String pid) {
        try (BufferedReader br = openResource("/warren/aandp/project02/login/professor.txt")) {
            String ln;
            while (br!=null && (ln=br.readLine())!=null) {
                String[] p = ln.trim().split(",");
                if (p[0].trim().equals(pid)) return p;
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    // open live or classpath resource
    private BufferedReader openResource(String resourcePath) {
        try {
            Path path = Paths.get("src/main/resources").resolve(resourcePath.substring(1));
            if (Files.exists(path)) return Files.newBufferedReader(path);
            InputStream in = getClass().getResourceAsStream(resourcePath);
            return (in!=null)?new BufferedReader(new InputStreamReader(in)):null;
        } catch (IOException e) { e.printStackTrace(); return null; }
    }

    // logout to start screen
    public void onLogoutButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("start.fxml")
        );
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(loader.load(), 640, 420);
        stage.setTitle("Sign Up or Login");
        stage.setScene(scene);
        stage.show();
    }
}