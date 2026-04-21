package Client.Controllers.AuctionPage;

import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuctionCreateController {
    @FXML
    private TextField itemNameInput;
    @FXML
    private TextField descriptionInput;

    public AuctionManager auction = AuctionManager.getInstance();

    @FXML
    private void createAuction() {
        String itemName = itemNameInput.getText();
        String description = descriptionInput.getText();
    }
}
