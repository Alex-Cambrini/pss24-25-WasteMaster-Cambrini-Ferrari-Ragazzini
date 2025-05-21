package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ScheduleController {

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            "wasteType", "scheduleType", "frequency", "pickupDate", "nextCollectionDate",
            "startDate", "status", "customerName", "customerSurname");

    private Timeline refreshTimeline;
    private ObservableList<ScheduleRow> allSchedules = FXCollections.observableArrayList();

    @FXML
    private CheckBox oneTimeCheckBox;
    @FXML
    private CheckBox recurringCheckBox;
    @FXML
    private CheckBox showDeletedCheckBox;
    // buttons
    @FXML
    private Button changeFrequencyButton;
    @FXML
    private Button toggleStatusButton;
    @FXML
    private Button deleteButton;

    @FXML
    private ContextMenu filterMenu;

    @FXML
    private TableView<ScheduleRow> scheduleTable;

    @FXML
    private TableColumn<ScheduleRow, String> wasteColumn;
    @FXML
    private TableColumn<ScheduleRow, String> typeColumn;
    @FXML
    private TableColumn<ScheduleRow, String> frequencyColumn;
    @FXML
    private TableColumn<ScheduleRow, String> dateColumn;
    @FXML
    private TableColumn<ScheduleRow, String> startColumn;
    @FXML
    private TableColumn<ScheduleRow, String> statusColumn;
    @FXML
    private TableColumn<ScheduleRow, String> customerColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        wasteColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleType"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("executionDate"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);
        showDeletedCheckBox.setSelected(false);

        loadSchedules();
        startAutoRefresh();
        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
        oneTimeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        showDeletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        scheduleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButtons(newVal);
        });

    }

    private void updateButtons(ScheduleRow selected) {
        if (selected != null) {
            boolean isRecurring = selected.getScheduleType() == ScheduleCategory.RECURRING;

            changeFrequencyButton.setDisable(!isRecurring);
            toggleStatusButton.setDisable(!isRecurring && selected == null);

            deleteButton.setDisable(false);

            ScheduleStatus status = selected.getStatus();

            switch (status) {
                case ACTIVE -> toggleStatusButton.setText("Pause");
                case PAUSED -> toggleStatusButton.setText("Resume");
                case COMPLETED, CANCELLED -> {
                    toggleStatusButton.setDisable(true);
                }
            }
        } else {
            changeFrequencyButton.setDisable(true);
            toggleStatusButton.setDisable(true);
            deleteButton.setDisable(true);
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
        all.forEach(s -> System.out.println(s));

        for (Schedule s : all) {
            if (s == null || s.getCustomer() == null || s.getWaste() == null || s.getScheduleStatus() == null)
                continue;

            ScheduleCategory scheduleType = s.getScheduleCategory();
            boolean isDeleted = s.getScheduleStatus() == ScheduleStatus.CANCELLED;

            boolean matchesType = (oneTimeCheckBox.isSelected() && scheduleType == ScheduleCategory.ONE_TIME)
                    || (recurringCheckBox.isSelected() && scheduleType == ScheduleCategory.RECURRING);
            boolean matchesDeleted = showDeletedCheckBox.isSelected() || !isDeleted;

            if (matchesType && matchesDeleted) {
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
            MainLayoutController.getInstance().setPageTitle("Add Schedule");
            AddScheduleController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/schedule/AddScheduleView.fxml");
            controller.setScheduleController(this);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Add Schedule view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangeFrequency() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to edit.");
            return;
        }

        if (selected.getScheduleType() == ScheduleCategory.ONE_TIME) {
            DialogUtils.showError("Edit not allowed", "Cannot edit one-time schedules.");
            return;
        }
        try {
            RecurringSchedule schedule = AppContext.recurringScheduleDAO.findById(selected.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/schedule/ChangeFrequencyDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

            dialogStage.setTitle("Change Frequency");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(mainStage);
            dialogStage.setScene(new Scene(root));

            ChangeFrequencyDialogController controller = loader.getController();
            controller.setSchedule(schedule);
            controller.setFrequencies(List.of(Frequency.values()));
            controller.setCurrentFrequency(schedule.getFrequency());

            dialogStage.showAndWait();

            Frequency selectedFreq = controller.getSelectedFrequency();

            if (selectedFreq != null) {
                AppContext.recurringScheduleManager.updateFrequency(schedule, selectedFreq);

            }
            loadSchedules();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteSchedule() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to delete.");
            return;
        }

        boolean success = false;

        try {
            switch (selected.getScheduleType()) {
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
                DialogUtils.showSuccess("Schedule cancelled successfully.");
                loadSchedules();
            } else {
                DialogUtils.showError("Cancellation failed", "Schedule could not be cancelled.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "An error occurred while cancelling the schedule.");
        }
    }

    @FXML
    private void handleSearch() {
        // String query = searchField.getText().toLowerCase().trim();

        // if (query.isEmpty()) {
        // scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
        // return;
        // }

        // ObservableList<ScheduleRow> filtered = FXCollections.observableArrayList();

        // for (ScheduleRow row : allSchedules) {
        // if ((activeFilters.contains("wasteType") &&
        // row.getWasteName().toLowerCase().contains(query)) ||
        // (activeFilters.contains("scheduleType") &&
        // row.getScheduleType().toLowerCase().contains(query)) ||
        // (activeFilters.contains("frequency") &&
        // row.getFrequency().toLowerCase().contains(query)) ||
        // (activeFilters.contains("pickupDate") &&
        // row.getExecutionDate().toLowerCase().contains(query)) ||
        // (activeFilters.contains("nextCollectionDate")
        // && row.getExecutionDate().toLowerCase().contains(query))
        // ||
        // (activeFilters.contains("startDate") &&
        // row.getStartDate().toLowerCase().contains(query)) ||
        // (activeFilters.contains("status") &&
        // row.getStatus().toLowerCase().contains(query)) ||
        // (activeFilters.contains("customer")
        // && row.getCustomer().toLowerCase().contains(query))) {
        // filtered.add(row);
        // }
        // }

        // scheduleTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll("wasteType", "scheduleType", "frequency", "pickupDate",
                "nextCollectionDate", "startDate", "status", "customerName", "customerSurname");
        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);

        loadSchedules();
    }

    @FXML
    private void showFilterMenu(javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { "wasteType", "scheduleType", "frequency", "pickupDate",
                "nextCollectionDate", "startDate", "status", "customerName", "customerSurname" };

        String[] labels = { "Waste", "Type", "Frequency", "Pickup", "Next", "Start", "Status", "Name", "Surname" };

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

    public void returnToScheduleView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance().loadCenter("/layouts/schedule/ScheduleView.fxml");
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Failed to load schedule view.");
        }
    }
}
