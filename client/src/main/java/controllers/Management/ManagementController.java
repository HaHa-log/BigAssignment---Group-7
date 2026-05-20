package controllers.Management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;

public abstract class ManagementController<T> {

    @FXML
    protected TableView<T> table;

    @FXML
    public void initialize() {
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
}