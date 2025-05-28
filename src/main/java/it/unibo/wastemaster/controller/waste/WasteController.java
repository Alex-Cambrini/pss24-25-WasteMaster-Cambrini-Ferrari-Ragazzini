package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.viewmodels.WasteRow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public class WasteController {

	@FXML
	private Button addWasteButton;

	@FXML
	private Button addProgramButton;

	@FXML
	private Button changeDayButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button deleteProgramButton;

	@FXML
	private Button resetSearchButton;

	@FXML
	private CheckBox showRecyclableCheckBox;

	@FXML
	private CheckBox showDangerousCheckBox;

	@FXML
	private TextField searchField;

	@FXML
	private TableView<WasteRow> wasteTable;

	@FXML
	private TableColumn<WasteRow, String> nameColumn;

	@FXML
	private TableColumn<WasteRow, Boolean> recyclableColumn;

	@FXML
	private TableColumn<WasteRow, Boolean> dangerousColumn;

	@FXML
	private TableColumn<WasteRow, DayOfWeek> dayOfWeekColumn;

	private final ObservableList<WasteRow> allWastes = FXCollections.observableArrayList();
	private Timeline refreshTimeline;

	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		recyclableColumn.setCellValueFactory(new PropertyValueFactory<>("recyclable"));
		dangerousColumn.setCellValueFactory(new PropertyValueFactory<>("dangerous"));
		dayOfWeekColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

		dayOfWeekColumn.setCellFactory(col -> new TableCell<WasteRow, DayOfWeek>() {
			@Override
			protected void updateItem(DayOfWeek item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : (item == null ? "-" : formatEnum(item)));
			}
		});

		loadWastes();
		startAutoRefresh();

		searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
		showRecyclableCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> handleSearch());
		showDangerousCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> handleSearch());

		addProgramButton.setDisable(true);
		changeDayButton.setDisable(true);
		deleteButton.setDisable(true);
		deleteProgramButton.setDisable(true);

		wasteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
			if (newRow == null) {
				addProgramButton.setDisable(true);
				changeDayButton.setDisable(true);
				deleteButton.setDisable(true);
				deleteProgramButton.setDisable(true);
				return;
			}

			boolean hasProgram = newRow.getDayOfWeek() != null;

			addProgramButton.setDisable(hasProgram);
			changeDayButton.setDisable(!hasProgram);
			deleteButton.setDisable(false);
			deleteProgramButton.setDisable(!hasProgram);
		});
	}

	private void startAutoRefresh() {
		refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> loadWastes()));
		refreshTimeline.setCycleCount(Timeline.INDEFINITE);
		refreshTimeline.play();
	}

	public void stopAutoRefresh() {
		if (refreshTimeline != null) {
			refreshTimeline.stop();
		}
	}

	private void loadWastes() {
		List<Waste> wastes = AppContext.getWasteManager().getActiveWastes();
		allWastes.clear();

		for (Waste waste : wastes) {
			WasteSchedule schedule = null;
			try {
				schedule = AppContext.getWasteScheduleManager().getWasteScheduleByWaste(waste);
			} catch (IllegalStateException ignored) {
			}
			allWastes.add(new WasteRow(waste, schedule));
		}

		wasteTable.setItems(FXCollections.observableArrayList(allWastes));

		if (!searchField.getText().isBlank()) {
			handleSearch();
		}
	}

	@FXML
	private void handleAddWaste() {
		try {
			Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

			Optional<AddWasteController> controllerOpt = DialogUtils.showModalWithController(
					"Add Waste",
					"/layouts/waste/AddWasteView.fxml",
					mainStage,
					ctrl -> {
					});

			if (controllerOpt.isPresent()) {
				loadWastes();
			}
		} catch (IOException e) {
			DialogUtils.showError("Navigation error", "Could not load Add Waste view.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleAddProgram() {
		try {
			WasteRow selectedRow = wasteTable.getSelectionModel().getSelectedItem();

			if (selectedRow == null || selectedRow.getDayOfWeek() != null) {
				return;
			}

			Waste selectedWaste = selectedRow.getWaste();

			Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

			Optional<AddProgramController> controllerOpt = DialogUtils.showModalWithController(
					"Add Program",
					"/layouts/waste/AddProgramView.fxml",
					mainStage,
					ctrl -> ctrl.setWaste(selectedWaste));

			if (controllerOpt.isPresent()) {
				loadWastes();
			}
		} catch (IOException e) {
			DialogUtils.showError("Navigation error", "Could not load Add Program view.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleChangeDay() {
		WasteRow selectedRow = wasteTable.getSelectionModel().getSelectedItem();

		if (selectedRow == null || selectedRow.getDayOfWeek() == null) {
			DialogUtils.showError("No Selection", "Please select a waste with a program assigned.",
					AppContext.getOwner());
			return;
		}

		try {
			WasteSchedule schedule = AppContext.getWasteScheduleManager().getWasteScheduleByWaste(selectedRow.getWaste());

			Optional<ChangeDayDialogController> controllerOpt = DialogUtils.showModalWithController(
					"Change Collection Day",
					"/layouts/waste/ChangeDayDialog.fxml",
					(Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow(),
					ctrl -> {
						ctrl.setSchedule(schedule);
						ctrl.setCurrentDay(schedule.getDayOfWeek());
					});

			if (controllerOpt.isPresent()) {
				DayOfWeek newDay = controllerOpt.get().getSelectedDay();
				if (newDay != null && newDay != schedule.getDayOfWeek()) {
					AppContext.getWasteScheduleManager().changeCollectionDay(schedule, newDay);
					loadWastes();
				}
			}
		} catch (IOException e) {
			DialogUtils.showError("Loading Error", "Failed to load the change day dialog.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDeleteWaste() {
		WasteRow selected = wasteTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			DialogUtils.showError("No Selection", "Please select a waste to delete.", AppContext.getOwner());
			return;
		}

		try {
			if (selected.getDayOfWeek() != null) {
				WasteSchedule schedule = AppContext.getWasteScheduleManager().getWasteScheduleByWaste(selected.getWaste());
				AppContext.getWasteScheduleDAO().delete(schedule);
			}
			AppContext.getWasteManager().softDeleteWaste(selected.getWaste());
			loadWastes();
		} catch (Exception e) {
			DialogUtils.showError("Error", "Failed to delete waste or its schedule.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDeleteProgramWaste() {
		WasteRow selected = wasteTable.getSelectionModel().getSelectedItem();
		if (selected == null || selected.getDayOfWeek() == null) {
			DialogUtils.showError("No Program", "Please select a waste with a program to delete.",
					AppContext.getOwner());
			return;
		}

		try {
			WasteSchedule schedule = AppContext.getWasteScheduleManager().getWasteScheduleByWaste(selected.getWaste());
			AppContext.getWasteScheduleDAO().delete(schedule);
			loadWastes();
		} catch (Exception e) {
			DialogUtils.showError("Error", "Failed to delete the program.", AppContext.getOwner());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleResetSearch() {
		searchField.clear();
		showRecyclableCheckBox.setSelected(false);
		showDangerousCheckBox.setSelected(false);
		wasteTable.setItems(FXCollections.observableArrayList(allWastes));
	}

	private void handleSearch() {
		String query = searchField.getText().toLowerCase().trim();

		ObservableList<WasteRow> filtered = FXCollections.observableArrayList();

		for (WasteRow row : allWastes) {
			boolean matchesName = row.getName().toLowerCase().contains(query);
			boolean matchesDayOfWeek = row.getDayOfWeek() != null &&
					formatEnum(row.getDayOfWeek()).toLowerCase().contains(query);

			boolean matchesCheckRecyclable = !showRecyclableCheckBox.isSelected() || row.isRecyclable();
			boolean matchesCheckDangerous = !showDangerousCheckBox.isSelected() || row.isDangerous();

			if ((query.isEmpty() || matchesName || matchesDayOfWeek)
					&& matchesCheckRecyclable
					&& matchesCheckDangerous) {
				filtered.add(row);
			}
		}

		wasteTable.setItems(filtered);
	}

	private String formatEnum(Enum<?> value) {
		if (value == null)
			return "";
		String lower = value.name().toLowerCase().replace("_", " ");
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}
}