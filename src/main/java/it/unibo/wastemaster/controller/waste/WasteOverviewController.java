package it.unibo.wastemaster.controller.waste;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WasteOverviewController {

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
	private TableView<?> wasteTable;

	@FXML
	private TableColumn<?, ?> nameColumn;

	@FXML
	private TableColumn<?, ?> recyclableColumn;

	@FXML
	private TableColumn<?, ?> dangerousColumn;

	@FXML
	private TableColumn<?, ?> dayOfWeekColumn;

	@FXML
	public void initialize() {
		// TODO
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
}