package controllers.MainPage.HomePage;

import models.*;
import controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomePageController {

    @FXML private Button managementNavigation;
    @FXML private Label welcomeLabel;

    public User user = SessionManager.getCurrentUser();

    @FXML
    private void initialize() {
        //If cannot load user
        managementNavigation.setVisible(false);
        managementNavigation.setManaged(false);
        welcomeLabel.setText("Welcome");

        User savedUser = SessionManager.getCurrentUser();
        if (savedUser != null) {
            setupUserData(savedUser);
        }
    }

    public void setupUserData(User loggedInUser) {
        this.user = loggedInUser;

        if (user != null) {
            boolean isAdmin = user instanceof Admin || user.isAdmin();

            managementNavigation.setVisible(isAdmin);
            managementNavigation.setManaged(isAdmin);

            welcomeLabel.setText("Welcome, " + user.getFullName());

            System.out.println("[UI Setup] Rendered views for type: " + user.getClass().getSimpleName());
            System.out.println("[UI Setup] Admin Privileges Status: " + isAdmin);
        }
    }

    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
    @FXML
    private void toInventory() {
        SceneManager.switchContent("/InventoryFXML/InventoryPage.fxml");
    }

    @FXML
    private void toManagementDashboard() {
        SceneManager.switchContent("/ManagementFXML/ManagementDashboard.fxml");
    }
}
