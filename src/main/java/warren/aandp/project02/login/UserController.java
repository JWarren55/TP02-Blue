package warren.aandp.project02.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.IOException;

public class UserController {
    private Stage stage;
    private Scene scene;
    public Label nameText;

    //Set name Text to "Hello" + User Name
    //When Log in is successful and opens user screen
    void setNameText(String text) {
        nameText.setText(text);
    }

    //Go back to start screen
    public void onLogOutButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderStart = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoaderStart.load(), 640, 420);
        stage.setTitle("Sign Up or Login");
        stage.setScene(scene);
        stage.show();
    }

}
