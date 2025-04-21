module warren.aandp.project02.login {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires jbcrypt;


    opens warren.aandp.project02.login to javafx.fxml;
    exports warren.aandp.project02.login;
}