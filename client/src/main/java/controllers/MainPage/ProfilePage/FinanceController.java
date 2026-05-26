package controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import exceptions.ApiException;
import services.UserApiService;

public class FinanceController extends BaseController {

    @FXML private VBox depositBox, withdrawBox;
    @FXML private TextField depositField, withdrawField;
    @FXML private Label balanceLabel, frozenBalanceLabel, totalBalanceLabel, depositStatus, withdrawStatus;

    private final UserApiService userApiService = new UserApiService();

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
            user = userApiService.deposit(user.getId(), amount);  // gọi server
            depositStatus.getStyleClass().setAll("success");
            depositStatus.setText("Deposited " + amount + " successfully!");
            refreshFinance();
        } catch (ApiException e) {
            depositStatus.getStyleClass().setAll("error");
            depositStatus.setText(e.getMessage());
        } catch (Exception e) {
            depositStatus.getStyleClass().setAll("error");
            depositStatus.setText("Invalid amount.");
        }
        depositField.clear();
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            user = userApiService.withdraw(user.getId(), amount);  // gọi server
            withdrawStatus.getStyleClass().setAll("success");
            withdrawStatus.setText("Withdrawn " + amount + " successfully!");
            refreshFinance();
        } catch (ApiException e) {
            withdrawStatus.getStyleClass().setAll("error");
            withdrawStatus.setText(e.getMessage());
        } catch (Exception e) {
            withdrawStatus.getStyleClass().setAll("error");
            withdrawStatus.setText("Invalid or exceeds balance.");
        }
        withdrawField.clear();
    }

    private void refreshFinance() {
        try {
            user = userApiService.getById(user.getId());  // fetch lại từ server
        } catch (Exception e) {
            e.printStackTrace();
        }
        balanceLabel.setText(String.format("%.2f", user.getBalance()));
        frozenBalanceLabel.setText(String.format("%.2f", user.getFrozenBalance()));
        totalBalanceLabel.setText(String.format("%.2f", user.getBalance() + user.getFrozenBalance()));
    }
    @FXML
    private void handleShowFrozenDetails() {
        
    }
}