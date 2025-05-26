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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;
import java.io.IOException;
import java.util.List;

public class VehicleController {

	private Timeline refreshTimeline;
	private ContextMenu filterMenu;

	private ObservableList<VehicleRow> allVehicles = FXCollections.observableArrayList();

	private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
			"plate", "brand", "model", "year", "licenceType", "vehicleStatus", "lastMaintenanceDate",
			"nextMaintenanceDate");

	@FXML
	private TextField searchField;

	@FXML
	private Button filterButton;

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
	private TableColumn<VehicleRow, Integer> capacityColumn;
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
		capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
		licenceTypeColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getLicenceType())));
		vehicleStatusColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getVehicleStatus())));
		lastMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastMaintenanceDate"));
		nextMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));

		loadVehicles();
		startAutoRefresh();

		searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
	}

	private void startAutoRefresh() {
		refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> loadVehicles()));
		refreshTimeline.setCycleCount(Timeline.INDEFINITE);
		refreshTimeline.play();
	}

	public void stopAutoRefresh() {
		if (refreshTimeline != null) {
			refreshTimeline.stop();
		}
	}

	private void loadVehicles() {
		List<Vehicle> vehicles = AppContext.vehicleDAO.findVehicleDetails();
		allVehicles.clear();

		for (Vehicle vehicle : vehicles) {
			allVehicles.add(new VehicleRow(vehicle));
		}

		VehicleTable.setItems(FXCollections.observableArrayList(allVehicles));

		if (!searchField.getText().isBlank()) {
			handleSearch();
		}
	}

	@FXML
	private void handleAddVehicle() {
		try {
			Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

			Optional<AddVehicleController> controllerOpt = DialogUtils.showModalWithController(
					"Add Vehicle",
					"/layouts/vehicle/AddVehicleView.fxml",
					mainStage,
					ctrl -> {
					});

			if (controllerOpt.isPresent()) {
				loadVehicles();
			}
		} catch (IOException e) {
			DialogUtils.showError("Navigation error", "Could not load Add Vehicle view.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleEditVehicle() {
		VehicleRow selected = VehicleTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			DialogUtils.showError("No Selection", "Please select a vehicle to edit.", AppContext.getOwner());
			return;
		}

		Vehicle vehicle = AppContext.vehicleManager.findVehicleByPlate(selected.getPlate());
		if (vehicle == null) {
			DialogUtils.showError("Not Found", "Vehicle not found.", AppContext.getOwner());
			return;
		}

		try {
			Optional<EditVehicleController> controllerOpt = DialogUtils.showModalWithController(
					"Edit Vehicle",
					"/layouts/vehicle/EditVehicleView.fxml",
					AppContext.getOwner(),
					ctrl -> ctrl.setVehicleToEdit(vehicle));

			if (controllerOpt.isPresent()) {
				loadVehicles();
			}
		} catch (Exception e) {
			DialogUtils.showError("Navigation error", "Could not load Edit view.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDeleteVehicle() {
		VehicleRow selected = VehicleTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			DialogUtils.showError("No Selection", "Please select a vehicle to delete.", AppContext.getOwner());
			return;
		}

		Vehicle vehicle = AppContext.vehicleManager.findVehicleByPlate(selected.getPlate());

		if (vehicle == null) {
			DialogUtils.showError("Not Found", "The selected vehicle could not be found.", AppContext.getOwner());
			return;
		}

		boolean success = AppContext.vehicleManager.deleteVehicle(vehicle);

		if (success) {
			DialogUtils.showSuccess("Vehicle deleted successfully.", AppContext.getOwner());
			loadVehicles();
		} else {
			DialogUtils.showError("Deletion Failed", "Unable to delete the selected vehicle.", AppContext.getOwner());
		}
	}

	@FXML
	private void handleSearch() {
		String query = searchField.getText().toLowerCase().trim();

		if (query.isEmpty()) {
			VehicleTable.setItems(FXCollections.observableArrayList(allVehicles));
			return;
		}

		ObservableList<VehicleRow> filtered = FXCollections.observableArrayList();

		for (VehicleRow row : allVehicles) {
			if ((activeFilters.contains("plate") && row.getPlate().toLowerCase().contains(query)) ||
					(activeFilters.contains("brand") && row.getBrand().toLowerCase().contains(query)) ||
					(activeFilters.contains("model") && row.getModel().toLowerCase().contains(query)) ||
					(activeFilters.contains("year") && String.valueOf(row.getRegistrationYear()).contains(query)) ||
					(activeFilters.contains("licenceType") && row.getLicenceType().name().equalsIgnoreCase(query)) ||
					(activeFilters.contains("vehicleStatus")
							&& formatEnum(row.getVehicleStatus()).toLowerCase().contains(query))
					||
					(activeFilters.contains("lastMaintenanceDate")
							&& row.getLastMaintenanceDate().toString().toLowerCase().contains(query))
					||
					(activeFilters.contains("nextMaintenanceDate")
							&& row.getNextMaintenanceDate().toString().toLowerCase().contains(query))) {
				filtered.add(row);
			}
		}

		VehicleTable.setItems(filtered);
	}

	@FXML
	private void handleResetSearch() {
		searchField.clear();

		activeFilters.clear();
		activeFilters.addAll(
				"plate", "brand", "model", "year",
				"licenceType", "vehicleStatus",
				"lastMaintenanceDate", "nextMaintenanceDate");

		loadVehicles();
	}

	@FXML
	private void showFilterMenu(MouseEvent event) {
		if (filterMenu != null && filterMenu.isShowing()) {
			filterMenu.hide();
			return;
		}

		filterMenu = new ContextMenu();

		String[] fields = {
				"plate", "brand", "model", "year", "licenceType",
				"vehicleStatus", "lastMaintenanceDate", "nextMaintenanceDate"
		};
		String[] labels = {
				"Plate", "Brand", "Model", "Year", "Licence",
				"Status", "Last Maint.", "Next Maint."
		};

		for (int i = 0; i < fields.length; i++) {
			String key = fields[i];
			String label = labels[i];

			CheckBox checkBox = new CheckBox(label);
			checkBox.setSelected(activeFilters.contains(key));

			checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
				if (isSelected) {
					if (!activeFilters.contains(key)) {
						activeFilters.add(key);
					}
				} else {
					activeFilters.remove(key);
				}
				handleSearch();
			});

			CustomMenuItem item = new CustomMenuItem(checkBox);
			item.setHideOnClick(false);
			filterMenu.getItems().add(item);
		}

		filterMenu.show(filterButton, event.getScreenX(), event.getScreenY());
	}

	private String formatEnum(Enum<?> value) {
		if (value == null)
			return "";
		String lower = value.name().toLowerCase().replace("_", " ");
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}
}
