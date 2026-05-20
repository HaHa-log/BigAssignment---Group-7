package Client.Controllers.Inventory;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

public class InventoryController {
    @FXML private StackPane contentArea;
    @FXML private ToggleButton listToggleButton, cardToggleButton;


    @FXML
    private void handleViewToggle() {
        /*
        if (listToggleButton.isSelected()) {
            tableView.setVisible(true);
            cardScrollPane.setVisible(false);
        } else if (cardToggleButton.isSelected()) {
            tableView.setVisible(false);
            cardScrollPane.setVisible(true);

           populateCards();
        }
         */
    }

}
