package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.viewmodels.VehicleRow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.util.List;

public class VehicleController {

	private Timeline refreshTimeline;

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
		licenceTypeColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getLicenceType())));
		vehicleStatusColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getVehicleStatus())));
		lastMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastMaintenanceDate"));
		nextMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));

		loadVehicles();
		startAutoRefresh();
	}

	private void startAutoRefresh() {
		refreshTimeline = new Timeline(
				new KeyFrame(Duration.seconds(30), event -> loadVehicles()));
		refreshTimeline.setCycleCount(Timeline.INDEFINITE);
		refreshTimeline.play();
	}

	public void stopAutoRefresh() {
		if (refreshTimeline != null) {
			refreshTimeline.stop();
		}
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
					row[4].toString(), // licenceType as string
					row[5].toString(), // vehicleStatus as string
					row[6].toString(), // lastMaintenanceDate
					row[7].toString() // nextMaintenanceDate
			));
		}

		VehicleTable.setItems(rows);
	}

	@FXML
	private void handleAddVehicle() {
		try {
			MainLayoutController.getInstance().setPageTitle("Add Vehicle");
			AddVehicleController controller = MainLayoutController.getInstance()
					.loadCenterWithController("/layouts/vehicle/AddVehicleView.fxml");
			controller.setVehicleController(this);
		} catch (Exception e) {
			DialogUtils.showError("Navigation error", "Could not load Add Vehicle view.");
			e.printStackTrace();
		}
	}

	private String formatEnum(String raw) {
		String lower = raw.toLowerCase().replace("_", " ");
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}

	@FXML
	private void handleDeleteVehicle() {
		VehicleRow selected = VehicleTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			DialogUtils.showError("No Selection", "Please select a vehicle to delete.");
			return;
		}

		Vehicle vehicle = AppContext.vehicleManager.findVehicleByPlate(selected.getPlate());

		if (vehicle == null) {
			DialogUtils.showError("Not Found", "The selected vehicle could not be found.");
			return;
		}

		boolean success = AppContext.vehicleManager.deleteVehicle(vehicle);

		if (success) {
			DialogUtils.showSuccess("Vehicle deleted successfully.");
			loadVehicles();
		} else {
			DialogUtils.showError("Deletion Failed", "Unable to delete the selected vehicle.");
		}
	}

	public void returnToVehicleView() {
		try {
			MainLayoutController.getInstance().restorePreviousTitle();
			MainLayoutController.getInstance().loadCenter("/layouts/vehicle/VehicleView.fxml");
		} catch (Exception e) {
			DialogUtils.showError("Navigation error", "Failed to load vehicle view.");
		}
	}

}