package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FinanceController extends BaseController {

    @FXML private VBox depositBox, withdrawBox;
    @FXML private TextField depositField, withdrawField;
    @FXML private Label balanceLabel, frozenBalanceLabel, totalBalanceLabel, depositStatus, withdrawStatus;

    @Override
    protected void initData() {
        refreshFinance();
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

            boolean success = user.depositMoney(amount);

            if (!success) {throw new Exception();}

            user.addTransaction("💰 DEPOSIT | +" + amount + " | Balance: " + String.format("%.2f", user.getBalance()));

            depositStatus.getStyleClass().setAll("success");
            depositStatus.setText("Deposited " + amount + " successfully!");

            refreshFinance();

        } catch (Exception e) {

            depositStatus.getStyleClass().setAll("error");
            depositStatus.setText("Invalid amount");
        }
        depositField.clear();
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            boolean success = user.withdrawMoney(amount);

            if (!success) {throw new Exception();}

            user.addTransaction("💸 WITHDRAW | -" + amount + " | Balance: " + String.format("%.2f", user.getBalance()));

            withdrawStatus.getStyleClass().setAll("success");
            withdrawStatus.setText("Withdrawn " + amount + " successfully!");

            refreshFinance();

        } catch (Exception e) {

            withdrawStatus.getStyleClass().setAll("error");
            withdrawStatus.setText("Invalid or exceeds balance");
        }

        withdrawField.clear();
    }

    private void refreshFinance() {

        balanceLabel.setText(String.format("%.2f", user.getBalance()));

        frozenBalanceLabel.setText(String.format("%.2f", user.getFrozenBalance()));

        totalBalanceLabel.setText(String.format("%.2f", user.getBalance() + user.getFrozenBalance()));
    }
}
