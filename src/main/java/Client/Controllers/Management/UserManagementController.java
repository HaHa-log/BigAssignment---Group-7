package Client.Controllers.Management;

import Branch.Member;
import Branch.User;
import model.UsersDAO;
import model.impl.DaoFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class UserManagementController extends ManagementController<User> {

    @FXML
    private TableColumn<User, Integer> userId;
    @FXML
    private TableColumn<User, String> username;
    @FXML
    private TableColumn<User, String> email;
    @FXML
    private TableColumn<User, String> role;

    private final UsersDAO userDb = DaoFactory.createUsersDAO();

    @Override
    protected void configureColumns() {
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    @Override
    protected List<User> fetchData() {
        return userDb.getAll();
    }
}