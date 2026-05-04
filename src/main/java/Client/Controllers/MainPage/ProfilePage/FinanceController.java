package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FinanceController extends BaseController {

    @FXML private VBox depositBox, withdrawBox;
    @FXML private TextField depositField, withdrawField;
    @FXML private Label balanceLabel, depositStatus, withdrawStatus;

    @Override
    protected void initData() {
        balanceLabel.setText(String.valueOf(user.getBalance()));
    }

    @FXML
    private void handleDeposit() {
        depositBox.setVisible(true);
        depositBox.setManaged(true);
        withdrawBox.setVisible(false);
        withdrawBox.setManaged(false);
    }

    @FXML
    private void handleWithdraw() {
        withdrawBox.setVisible(true);
        withdrawBox.setManaged(true);
        depositBox.setVisible(false);
        depositBox.setManaged(false);
    }

    @FXML
    private void handleSaveDeposit() {
        try {
            double amount = Double.parseDouble(depositField.getText());
            user.depositMoney(amount);
            depositStatus.setStyle("-fx-text-fill: green; " + "-fx-background-color: white; " +"-fx-font-weight: bold;"+ "-fx-border-color: green; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; " + "-fx-padding: 5;");
            depositStatus.setText("Deposited " + amount+" successfully!");
            balanceLabel.setText(String.valueOf(user.getBalance()));
        } catch (Exception e) {
            depositStatus.setStyle("-fx-text-fill: white; " + "-fx-background-color: #ef4444; "+"-fx-font-weight: bold;" + "-fx-background-radius: 5; " + "-fx-padding: 5;");
            depositStatus.setText("Invalid amount");
        }
        depositField.clear();
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            user.withdrawMoney(amount);
            withdrawStatus.setStyle("-fx-text-fill: red; " + "-fx-background-color: white; "+"-fx-font-weight: bold;" + "-fx-border-color: red; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; " + "-fx-padding: 5;");
            withdrawStatus.setText("Withdrawn " + amount);
            balanceLabel.setText(String.valueOf(user.getBalance()));
        } catch (Exception e) {
            withdrawStatus.setStyle("-fx-text-fill: white; "+"-fx-font-weight: bold;" + "-fx-background-color: #ef4444; " + "-fx-background-radius: 5; " + "-fx-padding: 5;");
            withdrawStatus.setText("Invalid or exceeds balance");
        }
        withdrawField.clear();
    }
}
