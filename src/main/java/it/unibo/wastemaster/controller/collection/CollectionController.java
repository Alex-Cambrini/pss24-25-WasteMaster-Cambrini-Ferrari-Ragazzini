package it.unibo.wastemaster.controller.collection;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.viewmodels.CollectionRow;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for displaying and filtering waste collections in the view.
 */
public final class CollectionController {

    @FXML
    private Label totalLabel;

    @FXML
    private Label completedLabel;

    @FXML
    private Label cancelledLabel;

    @FXML
    private Label activeLabel;

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
    private CheckBox showActiveCheckBox;

    private final ObservableList<CollectionRow> allSchedules =
            FXCollections.observableArrayList();

    private List<Collection> collections;

    /**
     * Sets the list of collections and refreshes the table and statistics.
     *
     * @param collections the list of collection entities to display
     */
    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
        refresh();
    }

    /**
     * Refreshes the collection table and statistics.
     */
    public void refresh() {
        loadCollections();
        updateStatusCounts();
        applyFilters();
    }

    /**
     * Initializes the view, column bindings, and filter checkboxes.
     */
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
        showActiveCheckBox.setSelected(true);

        showCompletedCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
        showCancelledCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
        showActiveCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
    }

    private void loadCollections() {
        if (collections == null) {
            return;
        }

        allSchedules.clear();
        for (Collection c : collections) {
            allSchedules.add(new CollectionRow(c));
        }
    }

    private void updateStatusCounts() {
        int total = allSchedules.size();
        int completed = 0;
        int cancelled = 0;
        int active = 0;

        for (CollectionRow c : allSchedules) {
            switch (c.getStatus()) {
                case COMPLETED -> completed++;
                case CANCELLED -> cancelled++;
                case ACTIVE -> active++;
                default -> {
                    // Do nothing for unknown statuses
                }
            }
        }

        totalLabel.setText("Total: " + total);
        completedLabel.setText("Completed: " + completed);
        cancelledLabel.setText("Cancelled: " + cancelled);
        activeLabel.setText("Active: " + active);
    }

    private void applyFilters() {
        ObservableList<CollectionRow> filtered = FXCollections.observableArrayList();
        for (CollectionRow row : allSchedules) {
            if (isStatusVisible(row)) {
                filtered.add(row);
            }
        }
        collectionTable.setItems(filtered);
    }

    private boolean isStatusVisible(final CollectionRow row) {
        return switch (row.getStatus()) {
            case COMPLETED -> showCompletedCheckBox.isSelected();
            case CANCELLED -> showCancelledCheckBox.isSelected();
            case ACTIVE -> showActiveCheckBox.isSelected();
            default -> false;
        };
    }
}
