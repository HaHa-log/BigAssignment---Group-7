package Client.Controllers.MainPage.HomePage;

import Branch.Admin;
import Branch.SessionManager;
import Branch.User;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomePageController {

    @FXML private Button managementNavigation;
    @FXML private Label welcomeLabel;

    public User user = SessionManager.getCurrentUser();

    @FXML
    private void initialize() {
        user = SessionManager.getCurrentUser();
        boolean isAdmin = user instanceof Admin || user != null && user.isAdmin();
        managementNavigation.setVisible(isAdmin);
        managementNavigation.setManaged(isAdmin);

        if (user == null) {
            welcomeLabel.setText("Welcome");
            return;
        }

        String userName = user.getFullName();
        welcomeLabel.setText("Welcome, " + userName);
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
