module BigAssignment.Group7 {
    requires javafx.controls;
    requires javafx.fxml;

    opens Client to javafx.graphics, javafx.fxml;
    opens Client.Controllers.LoginPage to javafx.fxml;

    exports Client;
}