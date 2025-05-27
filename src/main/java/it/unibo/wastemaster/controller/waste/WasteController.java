package it.unibo.wastemaster.controller.waste;

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
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.util.List;

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
				setText(empty || item == null ? "-" : formatEnum(item));
			}
		});

		loadWastes();
		startAutoRefresh();

		searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
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
		List<Waste> wastes = AppContext.wasteManager.getAllWastes();
		allWastes.clear();

		for (Waste waste : wastes) {
			WasteSchedule schedule = AppContext.wasteScheduleManager.getWasteScheduleByWaste(waste);
			allWastes.add(new WasteRow(waste, schedule));
		}

		wasteTable.setItems(FXCollections.observableArrayList(allWastes));

		if (!searchField.getText().isBlank()) {
			handleSearch();
		}
	}

	@FXML
	private void handleAddWaste() {
		// TODO
	}

	@FXML
	private void handleAddProgram() {
		// TODO
	}

	@FXML
	private void handleChangeDay() {
		// TODO
	}

	@FXML
	private void handleDeleteWaste() {
		// TODO
	}

	@FXML
	private void handleResetSearch() {
		// TODO
	}

	private void handleSearch() {
		// TODO
	}

	private String formatEnum(Enum<?> value) {
		if (value == null)
			return "";
		String lower = value.name().toLowerCase().replace("_", " ");
		return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
	}
}