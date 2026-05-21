module client {
        requires java.sql;
        requires java.desktop;
        requires java.prefs;
        requires javafx.fxml;
        requires javafx.graphics;
        requires javafx.controls;
        requires java.net.http;
        requires com.fasterxml.jackson.databind;
        requires com.fasterxml.jackson.datatype.jsr310;

        exports app to javafx.graphics;

        opens controllers to javafx.fxml;
        opens controllers.LoginPage to javafx.fxml;
        opens controllers.AuctionPage to javafx.fxml;
        opens controllers.MainPage.ProfilePage to javafx.fxml;
        opens controllers.MainPage.HomePage to javafx.fxml;
        opens controllers.Management to javafx.fxml;
        opens controllers.Inventory to javafx.fxml;

        opens models to javafx.fxml;

        opens services.dto.auth to com.fasterxml.jackson.databind;
        opens services.dto.auction to com.fasterxml.jackson.databind;
        opens services.dto.user to com.fasterxml.jackson.databind;
}
