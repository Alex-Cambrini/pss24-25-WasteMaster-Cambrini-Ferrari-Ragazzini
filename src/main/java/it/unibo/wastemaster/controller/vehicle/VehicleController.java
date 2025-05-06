package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.viewmodels.VehicleRow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class VehicleController {

	@FXML
	private TableView<VehicleRow> VehicleTable;

	@FXML
	private TableColumn<VehicleRow, String> plateColumn;
	@FXML
	private TableColumn<VehicleRow, String> brandColumn;
	@FXML
	private TableColumn<VehicleRow, String> modelColumn;
	@FXML
	private TableColumn<VehicleRow, Integer> yearColumn;
	@FXML
	private TableColumn<VehicleRow, String> licenceTypeColumn;
	@FXML
	private TableColumn<VehicleRow, String> vehicleStatusColumn;
	@FXML
	private TableColumn<VehicleRow, String> lastMaintenanceDateColumn;
	@FXML
	private TableColumn<VehicleRow, String> nextMaintenanceDateColumn;

	@FXML
	public void initialize() {
		plateColumn.setCellValueFactory(new PropertyValueFactory<>("plate"));
		brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
		modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("registrationYear"));
		licenceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("licenceType"));
		vehicleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleStatus"));
		lastMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastMaintenanceDate"));
		nextMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));

		loadVehicles();
	}

	private void loadVehicles() {
		List<Object[]> rawData = AppContext.vehicleDAO.findVehicleDetails();
		ObservableList<VehicleRow> rows = FXCollections.observableArrayList();

		for (Object[] row : rawData) {
			rows.add(new VehicleRow(
				(String) row[0], // plate
				(String) row[1], // brand
				(String) row[2], // model
				(Integer) row[3], // registrationYear
				(String) row[4], // licenceType
				(String) row[5], // vehicleStatus
				(String) row[6], // lastMaintenanceDate
				(String) row[7]  // nextMaintenanceDate
			));
		}

		VehicleTable.setItems(rows);
	}
}
