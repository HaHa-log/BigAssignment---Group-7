package controllers.Management;

import models.User;
import models.SessionManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import services.UserApiService;

import java.time.LocalDateTime;
import java.util.List;

public class UserManagementController extends ManagementController<User> {

    private final UserApiService userApiService = new UserApiService();

    @FXML
    private TableColumn<User, Integer> userId;
    @FXML
    private TableColumn<User, String> username;
    @FXML
    private TableColumn<User, String> email;
    @FXML
    private TableColumn<User, String> role;
    @FXML
    private TableColumn<User, Boolean> status;

    @Override
    protected void configureColumns() {
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));

        configureStatusColumn();
    }

    private void configureStatusColumn() {
        status.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(cellData.getValue().isBlocked()));

        status.setCellFactory(column -> createBlockedToggleCell());
    }

    private TableCell<User, Boolean> createBlockedToggleCell() {
        return new TableCell<>() {
            private final ToggleButton toggle = new ToggleButton();

            {
                toggle.setOnAction(event -> {
                    User targetUser = getTableRow().getItem();
                    if (targetUser == null) return;

                    boolean isNowBlocked = toggle.isSelected();

                    try {
                        if (isNowBlocked) {
                            userApiService.block(targetUser.getId());
                            // SỬA LỖI: Cập nhật trực tiếp trạng thái lên thực thể targetUser thông qua hàm setBlocked
                            // thay vì gọi hàm admin.blockUser(member) cũ của lớp Admin đã xóa
                            targetUser.setBlocked(LocalDateTime.now().plusDays(100));
                        } else {
                            userApiService.unblock(targetUser.getId());
                            targetUser.isUnblocked();
                        }
                    } catch (Exception e) {
                        toggle.setSelected(!isNowBlocked);
                        System.err.println(e.getMessage());
                        return;
                    }

                    updateToggleVisuals(isNowBlocked);
                    getTableView().refresh();
                });
            }

            private void updateToggleVisuals(boolean blocked) {
                toggle.setText(blocked ? "BLOCKED" : "ACTIVE");
                if (blocked) {
                    toggle.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c62828;");
                } else {
                    toggle.setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #2e7d32;");
                }
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    toggle.setSelected(item);
                    updateToggleVisuals(item);
                    setGraphic(toggle);
                }
            }
        };
    }

    @Override
    protected List<User> fetchData() {
        try {
            return userApiService.getAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return List.of();
        }
    }
}
