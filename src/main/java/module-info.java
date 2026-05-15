module BigAssignment.Group7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;
    requires com.zaxxer.hikari;
    requires org.slf4j;

    // Permissions for the main application entry point
    opens Client to javafx.graphics, javafx.fxml;
    opens Branch to javafx.base, javafx.fxml;

    opens Client.Controllers.Management to javafx.fxml;
    opens Client.Controllers to javafx.fxml;
    opens Client.Controllers.LoginPage to javafx.fxml;
    opens Client.Controllers.AuctionPage to javafx.fxml;
    opens Client.Controllers.MainPage.ProfilePage to javafx.fxml;
    opens Client.Controllers.MainPage.HomePage to javafx.fxml;

    exports Client;
}
