module client {
        requires java.sql;
        requires java.desktop;
        requires java.prefs;
        requires javafx.fxml;
        requires javafx.graphics;
        requires javafx.controls;
        requires java.net.http;
        requires com.fasterxml.jackson.databind;

        exports app to javafx.graphics;

        opens controllers to javafx.fxml;
        opens controllers.LoginPage to javafx.fxml;
        opens controllers.AuctionPage to javafx.fxml;
        opens controllers.MainPage.ProfilePage to javafx.fxml;
        opens controllers.Management to javafx.fxml;

        opens models to javafx.fxml;

        opens models.dto.auth to com.fasterxml.jackson.databind;
}
