package Client.Controllers.Management;

import Branch.Admin;
import Branch.SessionManager;
import Branch.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import model.UsersDAO;
import model.impl.DaoFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

public class UserManagementController extends ManagementController<User> {
    Admin admin = (Admin) SessionManager.getCurrentUser();

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

    private final UsersDAO userDb = DaoFactory.createUsersDAO();

    @Override
    protected void configureColumns() {
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));

       configureStatusColumn();
    }

    private void configureStatusColumn() {

        //get isBlocked status
        status.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(cellData.getValue().isBlocked()));

        status.setCellFactory(column -> createBlockedToggleCell());
    }

    private TableCell<User, Boolean> createBlockedToggleCell() {
        return new TableCell<>() {

            private final ToggleButton toggle = new ToggleButton();
            {
                toggle.setOnAction(event -> handleToggleAction());
            }
            private void handleToggleAction() {
                //get User
                User user = getTableView().getItems().get(getIndex());

                boolean blocked = toggle.isSelected();

                if (blocked) {
                    //currently automatically set the block period to 10 days
                    admin.blockUser(user, LocalDateTime.now().plusDays(100));
                } else {
                    admin.unblockUser(user);
                }

                updateToggleText(blocked);
                getTableView().refresh();
            }

            private void updateToggleText(boolean blocked) {
                toggle.setText(blocked ? "BLOCKED" : "ACTIVE");
            }

            //update toggle content
            @Override
            protected void updateItem(Boolean item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                toggle.setSelected(item);

                updateToggleText(item);

                setGraphic(toggle);
            }
        };
    }

    @Override
    protected List<User> fetchData() {
        return userDb.getAll();
    }
}