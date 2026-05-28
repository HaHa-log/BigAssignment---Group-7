package controllers.Management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import models.Admin;
import models.SessionManager;
import models.User;

import java.util.List;

public abstract class ManagementController<T> {

    @FXML
    protected TableView<T> table;

    protected User user = SessionManager.getCurrentUser();

    @FXML
    public void initialize() {
        configureUser();
        configureColumns();
        loadTableData();
        table.setPlaceholder(new Label("No data found."));
    }

    protected abstract void configureColumns();
    protected abstract List<T> fetchData();

    public void loadTableData() {
        List<T> data = fetchData();
        if (data != null) {
            ObservableList<T> observableList = FXCollections.observableArrayList(data);
            table.setItems(observableList);
        }
    }

    @FXML
    protected void handleRefresh() {
        loadTableData();
        System.out.println(this.getClass().getSimpleName() + " Refreshed");
    }

    protected Admin configureUser() {
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            return admin;
        }

        return null;
    }
}