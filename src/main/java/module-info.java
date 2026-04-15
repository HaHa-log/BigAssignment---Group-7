module BigAssignment.Group7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Permissions for the main application entry point
    opens Client to javafx.graphics, javafx.fxml;

    opens Client.Controllers to javafx.fxml;
    opens Client.Controllers.LoginPage to javafx.fxml;
    opens Client.Controllers.AuctionPage to javafx.fxml;
    opens Client.Controllers.MainPage to javafx.fxml;

    exports Client;
}