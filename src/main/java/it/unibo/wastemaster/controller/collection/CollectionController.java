package it.unibo.wastemaster.controller.collection;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.viewmodels.CollectionRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CollectionController {

    private ObservableList<CollectionRow> allSchedules = FXCollections.observableArrayList();

    private List<Collection> collections;

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
        loadCollections();
    }

    @FXML
    private TableView<CollectionRow> collectionTable;
    @FXML
    private TableColumn<CollectionRow, String> wasteNameColumn;
    @FXML
    private TableColumn<CollectionRow, String> dateColumn;
    @FXML
    private TableColumn<CollectionRow, String> ZoneColumn;
    @FXML
    private TableColumn<CollectionRow, String> statusColumn;
    @FXML
    private TableColumn<CollectionRow, String> customerColumn;

    @FXML
    public void initialize() {
        wasteNameColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("collectionDate"));
        ZoneColumn.setCellValueFactory(new PropertyValueFactory<>("zone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        collectionTable.setItems(allSchedules);
        loadCollections();
    }

    private void loadCollections() {
        if (collections == null)
            return;

        allSchedules.clear();
        for (Collection c : collections) {
            allSchedules.add(new CollectionRow(c));
        }
    }
}
