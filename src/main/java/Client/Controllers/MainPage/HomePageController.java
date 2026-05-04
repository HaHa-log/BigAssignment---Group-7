package Client.Controllers.MainPage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.AuctionPage.AuctionListController;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class HomePageController{
    @FXML
    private Button managementNavigation;
    @FXML
    private AuctionListController auctionListController;
    @FXML
    private Label welcomeLabel;
    public User user = SessionManager.getCurrentUser();

    @FXML
    private void initialize() {
        String userName = user.getFullName();
        welcomeLabel.setText("Welcome, " + userName);
    }
    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
}
