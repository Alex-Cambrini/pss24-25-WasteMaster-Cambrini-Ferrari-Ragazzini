package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.viewmodels.ScheduleRow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

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
    private TableColumn<ScheduleRow, String> pickupColumn;
    @FXML
    private TableColumn<ScheduleRow, String> nextColumn;
    @FXML
    private TableColumn<ScheduleRow, String> startColumn;
    @FXML
    private TableColumn<ScheduleRow, String> statusColumn;
    @FXML
    private TableColumn<ScheduleRow, String> customerNameColumn;
    @FXML
    private TableColumn<ScheduleRow, String> customerSurnameColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        wasteColumn.setCellValueFactory(new PropertyValueFactory<>("wasteType"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleType"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        pickupColumn.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));
        nextColumn.setCellValueFactory(new PropertyValueFactory<>("nextCollectionDate"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("customerSurname"));

        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);

        loadSchedules();
        startAutoRefresh();
        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
        oneTimeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> loadSchedules());

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

        if (oneTimeCheckBox.isSelected()) {
            List<OneTimeSchedule> oneTimeList = AppContext.oneTimeScheduleDAO.findAll();
            for (OneTimeSchedule s : oneTimeList) {
                if (s == null || s.getCustomer() == null || s.getWaste() == null || s.getScheduleStatus() == null)
                    continue;

                allSchedules.add(new ScheduleRow(
                        s.getWaste().getWasteName(),
                        s.getScheduleCategory().name(),
                        "-",
                        s.getPickupDate() != null ? s.getPickupDate().toString() : "-",
                        "-",
                        "-",
                        s.getScheduleStatus().name(),
                        s.getCustomer().getName(),
                        s.getCustomer().getSurname()));
            }
        }

        if (recurringCheckBox.isSelected()) {
            List<RecurringSchedule> recurringList = AppContext.recurringScheduleDAO.findAll();
            for (RecurringSchedule s : recurringList) {
                if (s == null || s.getCustomer() == null || s.getWaste() == null || s.getScheduleStatus() == null)
                    continue;

                allSchedules.add(new ScheduleRow(
                        s.getWaste().getWasteName(),
                        s.getScheduleCategory().name(),
                        s.getFrequency() != null ? s.getFrequency().name() : "-",
                        "-",
                        s.getNextCollectionDate() != null ? s.getNextCollectionDate().toString() : "-",
                        s.getStartDate() != null ? s.getStartDate().toString() : "-",
                        s.getScheduleStatus().name(),
                        s.getCustomer().getName(),
                        s.getCustomer().getSurname()));
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
    private void handleEditSchedule() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a schedule to edit.");
            return;
        }

        try {
            if ("ONE_TIME".equals(selected.getScheduleType())) {
                var schedule = AppContext.oneTimeScheduleDAO.findAll().stream()
                        .filter(s -> s.getCustomer().getName().equals(selected.getCustomerName())
                                && s.getCustomer().getSurname().equals(selected.getCustomerSurname())
                                && s.getPickupDate() != null
                                && s.getPickupDate().toString().equals(selected.getPickupDate()))
                        .findFirst().orElse(null);

                if (schedule != null) {
                    MainLayoutController.getInstance().setPageTitle("Edit One-Time Schedule");
                    EditScheduleController controller = MainLayoutController.getInstance()
                            .loadCenterWithController("/layouts/schedule/EditScheduleView.fxml");
                    controller.setScheduleToEdit(schedule);
                    controller.setScheduleController(this);
                }
            } else if ("RECURRING".equals(selected.getScheduleType())) {
                var schedule = AppContext.recurringScheduleDAO.findAll().stream()
                        .filter(s -> s.getCustomer().getName().equals(selected.getCustomerName())
                                && s.getCustomer().getSurname().equals(selected.getCustomerSurname())
                                && s.getStartDate() != null
                                && s.getStartDate().toString().equals(selected.getStartDate()))
                        .findFirst().orElse(null);

                if (schedule != null) {
                    MainLayoutController.getInstance().setPageTitle("Edit Recurring Schedule");
                    EditScheduleController controller = MainLayoutController.getInstance()
                            .loadCenterWithController("/layouts/schedule/EditScheduleView.fxml");
                    controller.setScheduleToEdit(schedule);
                    controller.setScheduleController(this);
                }
            }
        } catch (Exception e) {
            DialogUtils.showError("Error", "Could not load edit view.");
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

        try {
            boolean success = false;

            if ("ONE_TIME".equals(selected.getScheduleType())) {
                var schedule = AppContext.oneTimeScheduleDAO.findAll().stream()
                        .filter(s -> s.getCustomer().getName().equals(selected.getCustomerName())
                                && s.getCustomer().getSurname().equals(selected.getCustomerSurname())
                                && s.getPickupDate() != null
                                && s.getPickupDate().toString().equals(selected.getPickupDate()))
                        .findFirst().orElse(null);

                if (schedule != null) {
                    success = AppContext.oneTimeScheduleManager.updateStatusOneTimeSchedule(schedule,
                            ScheduleStatus.CANCELLED);
                }
            }

            if ("RECURRING".equals(selected.getScheduleType())) {
                var schedule = AppContext.recurringScheduleDAO.findAll().stream()
                        .filter(s -> s.getCustomer().getName().equals(selected.getCustomerName())
                                && s.getCustomer().getSurname().equals(selected.getCustomerSurname())
                                && s.getStartDate() != null
                                && s.getStartDate().toString().equals(selected.getStartDate()))
                        .findFirst().orElse(null);

                if (schedule != null) {
                    success = AppContext.recurringScheduleManager.updateStatusRecurringSchedule(schedule,
                            ScheduleStatus.CANCELLED);
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
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
            return;
        }

        ObservableList<ScheduleRow> filtered = FXCollections.observableArrayList();

        for (ScheduleRow row : allSchedules) {
            if ((activeFilters.contains("wasteType") && row.getWasteType().toLowerCase().contains(query)) ||
                    (activeFilters.contains("scheduleType") && row.getScheduleType().toLowerCase().contains(query)) ||
                    (activeFilters.contains("frequency") && row.getFrequency().toLowerCase().contains(query)) ||
                    (activeFilters.contains("pickupDate") && row.getPickupDate().toLowerCase().contains(query)) ||
                    (activeFilters.contains("nextCollectionDate")
                            && row.getNextCollectionDate().toLowerCase().contains(query))
                    ||
                    (activeFilters.contains("startDate") && row.getStartDate().toLowerCase().contains(query)) ||
                    (activeFilters.contains("status") && row.getStatus().toLowerCase().contains(query)) ||
                    (activeFilters.contains("customerName") && row.getCustomerName().toLowerCase().contains(query)) ||
                    (activeFilters.contains("customerSurname")
                            && row.getCustomerSurname().toLowerCase().contains(query))) {
                filtered.add(row);
            }
        }

        scheduleTable.setItems(filtered);
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
