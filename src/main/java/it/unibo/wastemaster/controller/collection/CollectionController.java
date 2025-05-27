package it.unibo.wastemaster.controller.collection;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.viewmodels.CollectionRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CollectionController {

    private ObservableList<CollectionRow> allSchedules = FXCollections.observableArrayList();
    private List<Collection> collections;

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
        refresh();
    }

    public void refresh() {
        loadCollections();
        updateStatusCounts();
        applyFilters();
    }

    @FXML
    private Label totalLabel;
    @FXML
    private Label completedLabel;
    @FXML
    private Label cancelledLabel;
    @FXML
    private Label failedLabel;
    @FXML
    private Label inProgressLabel;
    @FXML
    private Label pendingLabel;

    @FXML
    private TableView<CollectionRow> collectionTable;
    @FXML
    private TableColumn<CollectionRow, String> wasteNameColumn;
    @FXML
    private TableColumn<CollectionRow, String> dateColumn;
    @FXML
    private TableColumn<CollectionRow, String> zoneColumn;
    @FXML
    private TableColumn<CollectionRow, String> statusColumn;
    @FXML
    private TableColumn<CollectionRow, String> customerColumn;

    @FXML
    private CheckBox showCompletedCheckBox;
    @FXML
    private CheckBox showCancelledCheckBox;
    @FXML
    private CheckBox showFailedCheckBox;
    @FXML
    private CheckBox showInProgressCheckBox;
    @FXML
    private CheckBox showPendingCheckBox;

    @FXML
    public void initialize() {
        wasteNameColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("collectionDate"));
        zoneColumn.setCellValueFactory(new PropertyValueFactory<>("zone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        collectionTable.setItems(allSchedules);

        showCompletedCheckBox.setSelected(true);
        showCancelledCheckBox.setSelected(true);
        showFailedCheckBox.setSelected(true);
        showInProgressCheckBox.setSelected(true);
        showPendingCheckBox.setSelected(true);

        showCompletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showCancelledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showFailedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showInProgressCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showPendingCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
    }

    private void loadCollections() {
        if (collections == null) return;

        allSchedules.clear();
        for (Collection c : collections) {
            allSchedules.add(new CollectionRow(c));
        }
    }

    private void updateStatusCounts() {
        int total = allSchedules.size();
        int completed = 0;
        int cancelled = 0;
        int failed = 0;
        int inProgress = 0;
        int pending = 0;

        for (CollectionRow c : allSchedules) {
            switch (c.getStatus()) {
                case COMPLETED -> completed++;
                case CANCELLED -> cancelled++;
                case FAILED -> failed++;
                case IN_PROGRESS -> inProgress++;
                case PENDING -> pending++;
            }
        }

        totalLabel.setText("Total: " + total);
        completedLabel.setText("Completed: " + completed);
        cancelledLabel.setText("Cancelled: " + cancelled);
        failedLabel.setText("Failed: " + failed);
        inProgressLabel.setText("In progress: " + inProgress);
        pendingLabel.setText("Pending: " + pending);
    }

    private void applyFilters() {
    ObservableList<CollectionRow> filtered = FXCollections.observableArrayList();

    for (CollectionRow row : allSchedules) {
        switch (row.getStatus()) {
            case COMPLETED -> {
                if (showCompletedCheckBox.isSelected()) filtered.add(row);
            }
            case CANCELLED -> {
                if (showCancelledCheckBox.isSelected()) filtered.add(row);
            }
            case FAILED -> {
                if (showFailedCheckBox.isSelected()) filtered.add(row);
            }
            case IN_PROGRESS -> {
                if (showInProgressCheckBox.isSelected()) filtered.add(row);
            }
            case PENDING -> {
                if (showPendingCheckBox.isSelected()) filtered.add(row);
            }
        }
    }

    collectionTable.setItems(filtered);
}

}
