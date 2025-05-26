package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.controller.collection.CollectionController;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleCategory;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.viewmodels.ScheduleRow;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ScheduleController {

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            "wasteType", "frequency", "customer");

    private Timeline refreshTimeline;
    private ObservableList<ScheduleRow> allSchedules = FXCollections.observableArrayList();

    @FXML
    private CheckBox oneTimeCheckBox;
    @FXML
    private CheckBox recurringCheckBox;
    @FXML
    private CheckBox showDeletedCheckBox;
    @FXML
    private CheckBox showActiveCheckBox;
    @FXML
    private CheckBox showPausedCheckBox;
    @FXML
    private CheckBox showCompletedCheckBox;
    // buttons
    @FXML
    private Button changeFrequencyButton;
    @FXML
    private Button toggleStatusButton;
    @FXML
    private Button viewAssociatedCollectionsButton;
    @FXML
    private Button deleteButton;

    @FXML
    private ContextMenu filterMenu;

    @FXML
    private TableView<ScheduleRow> scheduleTable;

    @FXML
    private TableColumn<ScheduleRow, String> wasteNameColumn;
    @FXML
    private TableColumn<ScheduleRow, ScheduleCategory> scheduleCategoryColumn;
    @FXML
    private TableColumn<ScheduleRow, Frequency> frequencyColumn;
    @FXML
    private TableColumn<ScheduleRow, LocalDate> executionDateColumn;
    @FXML
    private TableColumn<ScheduleRow, LocalDate> startDateColumn;
    @FXML
    private TableColumn<ScheduleRow, ScheduleStatus> statusColumn;
    @FXML
    private TableColumn<ScheduleRow, String> customerColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        wasteNameColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        scheduleCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleCategory"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        executionDateColumn.setCellValueFactory(new PropertyValueFactory<>("executionDate"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);
        showDeletedCheckBox.setSelected(false);
        showActiveCheckBox.setSelected(true);
        showPausedCheckBox.setSelected(false);
        showCompletedCheckBox.setSelected(false);

        loadSchedules();
        startAutoRefresh();

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        oneTimeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        showDeletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        showActiveCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        showPausedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        showCompletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());

        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButtons(newVal);
        });
    }

    private void updateButtons(ScheduleRow selected) {
        if (selected != null) {
            boolean isRecurring = selected.getScheduleCategory() == ScheduleCategory.RECURRING;

            changeFrequencyButton.setDisable(!isRecurring);
            toggleStatusButton.setDisable(!isRecurring && selected == null);

            deleteButton.setDisable(false);
            viewAssociatedCollectionsButton.setDisable(false);

            ScheduleStatus status = selected.getStatus();

            switch (status) {
                case ACTIVE -> {
                    toggleStatusButton.setDisable(!isRecurring);
                    toggleStatusButton.setText("Pause");
                    changeFrequencyButton.setDisable(!isRecurring);
                }
                case PAUSED -> {
                    toggleStatusButton.setDisable(!isRecurring);
                    toggleStatusButton.setText("Resume");
                    changeFrequencyButton.setDisable(true);
                }
                case COMPLETED, CANCELLED -> {
                    toggleStatusButton.setDisable(true);
                    changeFrequencyButton.setDisable(true);
                    deleteButton.setDisable(true);
                }
            }
        } else {
            changeFrequencyButton.setDisable(true);
            toggleStatusButton.setDisable(true);
            deleteButton.setDisable(true);
            viewAssociatedCollectionsButton.setDisable(true);
        }
    }

    @FXML
    private void handleStatusToggle() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        RecurringSchedule recurring = AppContext.recurringScheduleDAO.findById(selected.getId());
        ScheduleStatus currentStatus = selected.getStatus();

        switch (currentStatus) {
            case ACTIVE:
                AppContext.recurringScheduleManager.updateStatusRecurringSchedule(recurring, ScheduleStatus.PAUSED);
                toggleStatusButton.setText("Resume");
                break;
            case PAUSED:
                AppContext.recurringScheduleManager.updateStatusRecurringSchedule(recurring, ScheduleStatus.ACTIVE);
                toggleStatusButton.setText("Pause");
                break;
            default:
                break;
        }
        loadSchedules();
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> loadSchedules()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadSchedules() {
        allSchedules.clear();
        List<Schedule> all = AppContext.scheduleDAO.findAll();

        for (Schedule s : all) {
            if (s == null || s.getCustomer() == null || s.getWaste() == null || s.getScheduleStatus() == null)
                continue;

            ScheduleCategory scheduleType = s.getScheduleCategory();
            ScheduleStatus status = s.getScheduleStatus();

            boolean isOneTime = oneTimeCheckBox.isSelected() && scheduleType == ScheduleCategory.ONE_TIME;
            boolean isRecurring = recurringCheckBox.isSelected() && scheduleType == ScheduleCategory.RECURRING;
            boolean matchesType = isOneTime || isRecurring;

            boolean matchesStatus = (status == ScheduleStatus.CANCELLED && showDeletedCheckBox.isSelected()) ||
                    (status == ScheduleStatus.ACTIVE && showActiveCheckBox.isSelected()) ||
                    (status == ScheduleStatus.PAUSED && showPausedCheckBox.isSelected()) ||
                    (status == ScheduleStatus.COMPLETED && showCompletedCheckBox.isSelected());

            if (matchesType && matchesStatus) {
                if (scheduleType == ScheduleCategory.ONE_TIME)
                    allSchedules.add(new ScheduleRow((OneTimeSchedule) s));
                else if (scheduleType == ScheduleCategory.RECURRING)
                    allSchedules.add(new ScheduleRow((RecurringSchedule) s));
            }
        }

        scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
        handleSearch();
    }

    @FXML
    private void handleAddSchedule() {
        try {
            Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

            Optional<AddScheduleController> controllerOpt = DialogUtils.showModalWithController(
                    "Add Schedule",
                    "/layouts/schedule/AddScheduleView.fxml",
                    mainStage,
                    ctrl -> ctrl.setScheduleController(this));

            if (controllerOpt.isPresent()) {
                loadSchedules();
            }
        } catch (IOException e) {
            DialogUtils.showError("Loading Error", "Could not load Add Schedule dialog.", AppContext.getOwner());
        }
    }

    @FXML
    private void handleChangeFrequency() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to edit.", AppContext.getOwner());
            return;
        }
        if (selected.getScheduleCategory() == ScheduleCategory.ONE_TIME) {
            DialogUtils.showError("Edit not allowed", "Cannot edit one-time schedules.", AppContext.getOwner());
            return;
        }

        try {
            RecurringSchedule schedule = AppContext.recurringScheduleDAO.findById(selected.getId());
            if (schedule == null) {
                DialogUtils.showError("Error", "Schedule not found.", AppContext.getOwner());
                return;
            }

            Optional<ChangeFrequencyDialogController> controllerOpt = DialogUtils.showModalWithController(
                    "Change Frequency",
                    "/layouts/schedule/ChangeFrequencyDialog.fxml",
                    (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow(),
                    ctrl -> {
                        ctrl.setSchedule(schedule);
                        ctrl.setFrequencies(List.of(Frequency.values()));
                        ctrl.setCurrentFrequency(schedule.getFrequency());
                    });

            if (controllerOpt.isPresent()) {
                Frequency newFreq = controllerOpt.get().getSelectedFrequency();
                if (newFreq != null) {
                    AppContext.recurringScheduleManager.updateFrequency(schedule, newFreq);
                    loadSchedules();
                }
            }
        } catch (IOException e) {
            DialogUtils.showError("Loading Error", "Failed to load the dialog.", AppContext.getOwner());
        } catch (Exception e) {
            DialogUtils.showError("Unexpected Error", "An unexpected error occurred.", AppContext.getOwner());
        }
    }

    @FXML
    private void handleDeleteSchedule() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to delete.", AppContext.getOwner());
            return;
        }

        boolean success = false;

        try {
            switch (selected.getScheduleCategory()) {
                case ONE_TIME -> {
                    OneTimeSchedule schedule = AppContext.oneTimeScheduleDAO.findById(selected.getId());
                    if (schedule != null) {
                        success = AppContext.oneTimeScheduleManager.softDeleteOneTimeSchedule(schedule);
                    }
                }

                case RECURRING -> {
                    RecurringSchedule schedule = AppContext.recurringScheduleDAO.findById(selected.getId());
                    if (schedule != null) {
                        success = AppContext.recurringScheduleManager
                                .updateStatusRecurringSchedule(schedule, ScheduleStatus.CANCELLED);
                    }
                }
            }

            if (success) {
                DialogUtils.showSuccess("Schedule cancelled successfully.", AppContext.getOwner());
                loadSchedules();
            } else {
                DialogUtils.showError("Cancellation failed", "Schedule could not be cancelled.", AppContext.getOwner());
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "An error occurred while cancelling the schedule.", AppContext.getOwner());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
            return;
        }

        ObservableList<ScheduleRow> filtered = FXCollections.observableArrayList();

        for (ScheduleRow row : allSchedules) {
            if ((activeFilters.contains("wasteType") &&
                    row.getWasteName().toLowerCase().contains(query)) ||

                    (activeFilters.contains("frequency") &&
                            row.getFrequency() != null &&
                            row.getFrequency().name().toLowerCase().contains(query))
                    ||

                    (activeFilters.contains("customer") &&
                            row.getCustomer().toLowerCase().contains(query))) {
                filtered.add(row);
            }
        }

        scheduleTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll("wasteType", "frequency", "customer");
        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);

        loadSchedules();
    }

    @FXML
    private void handleViewAssociatedCollections() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to view associated collection.",
                    AppContext.getOwner());
            return;
        }

        Schedule schedule;
        if (selected.getScheduleCategory() == ScheduleCategory.ONE_TIME) {
            schedule = AppContext.oneTimeScheduleDAO.findById(selected.getId());
        } else {
            schedule = AppContext.recurringScheduleDAO.findById(selected.getId());
        }

        List<Collection> collections = AppContext.collectionManager.getAllCollectionBySchedule(schedule);

        try {
            MainLayoutController.getInstance().setPageTitle("Associated Collections");
            CollectionController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/collection/CollectionView.fxml");
            controller.setCollections(collections);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Associated Collections view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    @FXML
    private void showFilterMenu(javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { "wasteType", "frequency", "customer" };
        String[] labels = { "Waste", "Frequency", "Customer" };

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

        filterMenu.show(scheduleTable, event.getScreenX(), event.getScreenY());
    }
}
