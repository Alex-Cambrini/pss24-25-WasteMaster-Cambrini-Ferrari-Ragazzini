package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.service.WasteManager;
import it.unibo.wastemaster.domain.service.WasteScheduleManager;
import it.unibo.wastemaster.viewmodels.WasteRow;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for managing the waste types and their collection schedules.
 * Handles loading, displaying, searching, filtering, adding, editing, and deleting waste types and programs.
 * Supports periodic automatic refresh of the waste list and interaction with the JavaFX UI.
 */
public final class WasteController implements AutoRefreshable {

    private static final int REFRESH_INTERVAL_SECONDS = 30;
    private static final String ERROR_TITLE = "Error";
    private final ObservableList<WasteRow> allWastes = FXCollections.observableArrayList();

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

    private Timeline refreshTimeline;
    private WasteManager wasteManager;
    private WasteScheduleManager wasteScheduleManager;

    /**
     * Sets the waste manager used for waste operations.
     *
     * @param wasteManager the WasteManager to use
     */
    public void setWasteManager(WasteManager wasteManager) {
        this.wasteManager = wasteManager;
    }

    /**
     * Sets the waste schedule manager used for program operations.
     *
     * @param wasteScheduleManager the WasteScheduleManager to use
     */
    public void setWasteScheduleManager(WasteScheduleManager wasteScheduleManager) {
        this.wasteScheduleManager = wasteScheduleManager;
    }

    /**
     * Initializes the controller and binds table and filter logic.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recyclableColumn.setCellValueFactory(new PropertyValueFactory<>("recyclable"));
        dangerousColumn.setCellValueFactory(new PropertyValueFactory<>("dangerous"));
        dayOfWeekColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        dayOfWeekColumn.setCellFactory(
                col -> new TableCell<WasteRow, DayOfWeek>() {
                    @Override
                    protected void updateItem(final DayOfWeek item, final boolean empty) {
                        super.updateItem(item, empty);
                        String displayText;
                        if (empty) {
                            displayText = null;
                        } else if (item == null) {
                            displayText = "-";
                        } else {
                            displayText = formatEnum(item);
                        }
                        setText(displayText);
                    }
                });

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
        showRecyclableCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        showDangerousCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> handleSearch());

        addProgramButton.setDisable(true);
        changeDayButton.setDisable(true);
        deleteButton.setDisable(true);
        deleteProgramButton.setDisable(true);

        wasteTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldRow, newRow) -> {
                    if (newRow == null) {
                        addProgramButton.setDisable(true);
                        changeDayButton.setDisable(true);
                        deleteButton.setDisable(true);
                        deleteProgramButton.setDisable(true);
                        return;
                    }

                    final boolean hasProgram = newRow.getDayOfWeek() != null;
                    addProgramButton.setDisable(hasProgram);
                    changeDayButton.setDisable(!hasProgram);
                    deleteButton.setDisable(false);
                    deleteProgramButton.setDisable(!hasProgram);
                });
    }

    /**
     * Initializes data and loads the waste list.
     */
    public void initData() {
        loadWastes();
    }

    /**
     * Starts the automatic refresh of the waste list every fixed interval.
     */
    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null || wasteManager == null || wasteScheduleManager == null) {
            return;
        }
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS), event -> loadWastes()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh if it is running.
     */
    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Loads all waste types and their schedules, updating the table.
     */
    private void loadWastes() {
        List<Waste> wastes = wasteManager.getActiveWastes();
        allWastes.clear();

        for (Waste waste : wastes) {
            WasteSchedule schedule = null;
            try {
                schedule = wasteScheduleManager.getWasteScheduleByWaste(waste);
            } catch (IllegalStateException ignored) {
                // It's acceptable if no schedule exists for this waste; skip it.
            }
            allWastes.add(new WasteRow(waste, schedule));
        }

        wasteTable.setItems(FXCollections.observableArrayList(allWastes));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    /**
     * Handles the action for adding a new waste type.
     */
    @FXML
    private void handleAddWaste() {
        try {
            final Stage mainStage = (Stage) MainLayoutController.getInstance()
                    .getRootPane().getScene().getWindow();

            final Optional<AddWasteController> controllerOpt = DialogUtils.showModalWithController(
                    "Add Waste",
                    "/layouts/waste/AddWasteView.fxml",
                    mainStage,
                    ctrl -> {
                        ctrl.setWasteManager(wasteManager);
                    });

            if (controllerOpt.isPresent()) {
                loadWastes();
            }
        } catch (final IOException e) {
            DialogUtils.showError(
                    "Navigation error",
                    "Could not load Add Waste view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action for adding a collection program to the selected waste.
     */
    @FXML
    private void handleAddProgram() {
        try {
            final WasteRow selectedRow = wasteTable.getSelectionModel().getSelectedItem();

            if (selectedRow == null || selectedRow.getDayOfWeek() != null) {
                return;
            }

            final Waste selectedWaste = selectedRow.getWaste();
            final Stage mainStage = (Stage) MainLayoutController.getInstance()
                    .getRootPane().getScene().getWindow();

            final Optional<AddProgramController> controllerOpt = DialogUtils.showModalWithController(
                    "Add Program",
                    "/layouts/waste/AddProgramView.fxml",
                    mainStage,
                    ctrl -> {
                        ctrl.setWaste(selectedWaste);
                        ctrl.setWasteScheduleManager(wasteScheduleManager);
                    });

            if (controllerOpt.isPresent()) {
                loadWastes();
            }
        } catch (final IOException e) {
            DialogUtils.showError(
                    "Navigation error",
                    "Could not load Add Program view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of changing the collection day for the selected waste.
     */
    @FXML
    private void handleChangeDay() {
        final WasteRow selectedRow = wasteTable.getSelectionModel().getSelectedItem();

        if (selectedRow == null || selectedRow.getDayOfWeek() == null) {
            DialogUtils.showError(
                    "No Selection",
                    "Please select a waste with a program assigned.",
                    AppContext.getOwner());
            return;
        }

        try {
            final WasteSchedule schedule = wasteScheduleManager.getWasteScheduleByWaste(selectedRow.getWaste());

            final Stage stage = (Stage) MainLayoutController.getInstance()
                    .getRootPane()
                    .getScene()
                    .getWindow();

            final Optional<ChangeDayDialogController> controllerOpt = DialogUtils.showModalWithController(
                    "Change Collection Day",
                    "/layouts/waste/ChangeDayDialog.fxml",
                    stage,
                    ctrl -> {
                        ctrl.setSchedule(schedule);
                        ctrl.setCurrentDay(schedule.getDayOfWeek());
                    });

            if (controllerOpt.isPresent()) {
                final DayOfWeek newDay = controllerOpt.get().getSelectedDay();
                if (newDay != null && !newDay.equals(schedule.getDayOfWeek())) {
                    wasteScheduleManager.changeCollectionDay(schedule, newDay);
                    loadWastes();
                }
            }
        } catch (final IOException e) {
            DialogUtils.showError(
                    ERROR_TITLE,
                    "Failed to load the change day dialog.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action to delete the selected waste and its program if present.
     */
    @FXML
    private void handleDeleteWaste() {
        WasteRow selected = wasteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(
                    "No Selection", "Please select a waste to delete.",
                    AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete this waste?",
                AppContext.getOwner());

        if (!confirmed) {
            return;
        }

        try {
            if (selected.getDayOfWeek() != null) {
                WasteSchedule schedule = wasteScheduleManager.getWasteScheduleByWaste(selected.getWaste());
                wasteScheduleManager.deleteSchedule(schedule);
            }
            wasteManager.softDeleteWaste(selected.getWaste());
            loadWastes();
        } catch (Exception e) {
            DialogUtils.showError(
                    ERROR_TITLE, "Failed to delete waste or its schedule.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action to delete only the collection program for the selected waste.
     */
    @FXML
    private void handleDeleteProgramWaste() {
        WasteRow selected = wasteTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getDayOfWeek() == null) {
            DialogUtils.showError("No Program",
                    "Please select a waste with a program to delete.",
                    AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete the collection program for this waste?",
                AppContext.getOwner());

        if (!confirmed) {
            return;
        }

        try {
            WasteSchedule schedule = wasteScheduleManager.getWasteScheduleByWaste(selected.getWaste());
            wasteScheduleManager.deleteSchedule(schedule);
            loadWastes();
        } catch (Exception e) {
            DialogUtils.showError(
                    ERROR_TITLE, "Failed to delete the program.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the reset of the search field and filter checkboxes.
     */
    @FXML
    private void handleResetSearch() {
        searchField.clear();
        showRecyclableCheckBox.setSelected(false);
        showDangerousCheckBox.setSelected(false);
        wasteTable.setItems(FXCollections.observableArrayList(allWastes));
    }

    /**
     * Handles the search/filtering of wastes based on the search field and active filters.
     */
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        ObservableList<WasteRow> filtered = FXCollections.observableArrayList();

        for (WasteRow row : allWastes) {
            boolean matchesName = row.getName().toLowerCase().contains(query);
            boolean matchesDayOfWeek = row.getDayOfWeek() != null
                    && formatEnum(row.getDayOfWeek()).toLowerCase().contains(query);

            boolean matchesCheckRecyclable = !showRecyclableCheckBox.isSelected()
                    || row.isRecyclable();
            boolean matchesCheckDangerous = !showDangerousCheckBox.isSelected()
                    || row.isDangerous();

            if ((query.isEmpty() || matchesName || matchesDayOfWeek)
                    && matchesCheckRecyclable
                    && matchesCheckDangerous) {
                filtered.add(row);
            }
        }

        wasteTable.setItems(filtered);
    }

    /**
     * Formats enum values for display in the table.
     *
     * @param value the enum value
     * @return the formatted string
     */
    private String formatEnum(final Enum<?> value) {
        if (value == null) {
            return "";
        }
        String lower = value.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
