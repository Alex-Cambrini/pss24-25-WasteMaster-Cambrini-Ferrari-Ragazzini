package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.collection.CollectionController;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule.Frequency;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.model.Schedule.ScheduleCategory;
import it.unibo.wastemaster.domain.model.Schedule.ScheduleStatus;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.OneTimeScheduleManager;
import it.unibo.wastemaster.domain.service.RecurringScheduleManager;
import it.unibo.wastemaster.domain.service.ScheduleManager;
import it.unibo.wastemaster.viewmodels.ScheduleRow;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for managing schedules in the WasteMaster application.
 * Handles schedule creation, editing, deletion, filtering, and display logic.
 * Supports both one-time and recurring schedules, and provides search/filter UI.
 */
public final class ScheduleController implements AutoRefreshable {

    private static final int REFRESH_INTERVAL_SECONDS = 30;
    private static final String FILTER_WASTE_TYPE = "wasteType";
    private static final String FILTER_FREQUENCY = "frequency";
    private static final String FILTER_CUSTOMER = "customer";
    private static final String TITLE_NO_SELECTION = "No Selection";
    private final ObservableList<String> activeFilters = FXCollections
            .observableArrayList(FILTER_WASTE_TYPE, FILTER_FREQUENCY, FILTER_CUSTOMER);
    private final ObservableList<ScheduleRow> allSchedules = FXCollections.observableArrayList();

    private CollectionManager collectionManager;
    private OneTimeScheduleManager oneTimeScheduleManager;
    private RecurringScheduleManager recurringScheduleManager;
    private ScheduleManager scheduleManager;
    private Timeline refreshTimeline;

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

    @FXML
    private Button changeFrequencyButton;

    @FXML
    private Button toggleStatusButton;

    @FXML
    private Button showRelatedCollectionsButton;

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

    /**
     * Sets the schedule manager used for schedule operations.
     *
     * @param scheduleManager the ScheduleManager to use
     */
    public void setScheduleManager(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    /**
     * Sets the recurring schedule manager used for recurring schedule operations.
     *
     * @param recurringScheduleManager the RecurringScheduleManager to use
     */
    public void setRecurringScheduleManager(
            RecurringScheduleManager recurringScheduleManager) {
        this.recurringScheduleManager = recurringScheduleManager;
    }

    /**
     * Sets the one-time schedule manager used for one-time schedule operations.
     *
     * @param oneTimeScheduleManager the OneTimeScheduleManager to use
     */
    public void setOneTimeScheduleManager(OneTimeScheduleManager oneTimeScheduleManager) {
        this.oneTimeScheduleManager = oneTimeScheduleManager;
    }

    /**
     * Sets the collection manager used for retrieving collections.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up table columns, checkboxes, and listeners.
     */
    @FXML
    public void initialize() {
        wasteNameColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        scheduleCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleCategory"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_FREQUENCY));
        executionDateColumn.setCellValueFactory(new PropertyValueFactory<>("executionDate"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_CUSTOMER));

        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);
        showDeletedCheckBox.setSelected(false);
        showActiveCheckBox.setSelected(true);
        showPausedCheckBox.setSelected(false);
        showCompletedCheckBox.setSelected(false);

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        oneTimeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        recurringCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showDeletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showActiveCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showPausedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());
        showCompletedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refresh());

        scheduleTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> updateButtons(newVal));
    }

    /**
     * Refreshes the schedule table by reloading schedules and applying the current search filter.
     */
    public void refresh() {
        loadSchedules();
        handleSearch();
    }

    /**
     * Initializes data loading and starts automatic refresh.
     * This should be called from the main controller after dependencies are set.
     */
    public void initData() {
        refresh();
    }

    private void updateButtons(final ScheduleRow selected) {
        if (selected != null) {
            boolean isRecurring = selected.getScheduleCategory() == ScheduleCategory.RECURRING;

            changeFrequencyButton.setDisable(!isRecurring);
            toggleStatusButton.setDisable(!isRecurring && selected == null);

            deleteButton.setDisable(false);
            showRelatedCollectionsButton.setDisable(false);

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
                default -> {
                    toggleStatusButton.setDisable(true);
                    changeFrequencyButton.setDisable(true);
                    deleteButton.setDisable(true);
                }
            }
        } else {
            changeFrequencyButton.setDisable(true);
            toggleStatusButton.setDisable(true);
            deleteButton.setDisable(true);
            showRelatedCollectionsButton.setDisable(true);
        }
    }

    /**
     * Handles toggling the status (pause/resume) of a recurring schedule.
     */
    @FXML
    private void handleStatusToggle() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Optional<RecurringSchedule> recurringOpt = recurringScheduleManager.findRecurringScheduleById(selected.getId());
        if (recurringOpt.isEmpty()) {
            DialogUtils.showError("Error",
                    "Unable to find the selected recurring schedule.",
                    AppContext.getOwner());
            return;
        }
        RecurringSchedule recurring = recurringOpt.get();
        ScheduleStatus currentStatus = selected.getStatus();

        switch (currentStatus) {
            case ACTIVE:
                recurringScheduleManager.updateStatusRecurringSchedule(recurring,
                        ScheduleStatus.PAUSED);
                toggleStatusButton.setText("Resume");
                break;
            case PAUSED:
                recurringScheduleManager.updateStatusRecurringSchedule(recurring,
                        ScheduleStatus.ACTIVE);
                toggleStatusButton.setText("Pause");
                break;
            default:
                break;
        }
        loadSchedules();
    }

    /**
     * Starts the automatic refresh of the schedule table.
     */
    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null || scheduleManager == null) {
            return;
        }
        refreshTimeline = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_INTERVAL_SECONDS),
                e -> loadSchedules()));
        refreshTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the auto-refresh timeline if it is running.
     */
    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    private void loadSchedules() {
        allSchedules.clear();
        List<Schedule> all = scheduleManager.findAllSchedule();

        for (Schedule s : all) {
            if (shouldIncludeSchedule(s)) {
                ScheduleCategory scheduleType = s.getScheduleCategory();
                if (scheduleType == ScheduleCategory.ONE_TIME) {
                    allSchedules.add(new ScheduleRow((OneTimeSchedule) s));
                } else if (scheduleType == ScheduleCategory.RECURRING) {
                    allSchedules.add(new ScheduleRow((RecurringSchedule) s));
                }
            }
        }

        scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
    }

    private boolean shouldIncludeSchedule(final Schedule s) {
        if (s == null || s.getCustomer() == null || s.getWaste() == null
                || s.getScheduleStatus() == null) {
            return false;
        }

        ScheduleCategory scheduleType = s.getScheduleCategory();
        ScheduleStatus status = s.getScheduleStatus();

        boolean isOneTime = oneTimeCheckBox.isSelected() && scheduleType == ScheduleCategory.ONE_TIME;
        boolean isRecurring = recurringCheckBox.isSelected()
                && scheduleType == ScheduleCategory.RECURRING;
        boolean matchesType = isOneTime || isRecurring;

        boolean matchesStatus = (status == ScheduleStatus.CANCELLED
                && showDeletedCheckBox.isSelected())
                || (status == ScheduleStatus.ACTIVE && showActiveCheckBox.isSelected())
                || (status == ScheduleStatus.PAUSED && showPausedCheckBox.isSelected())
                || (status == ScheduleStatus.COMPLETED
                        && showCompletedCheckBox.isSelected());

        return matchesType && matchesStatus;
    }

    /**
     * Handles the action to add a new schedule.
     */
    @FXML
    private void handleAddSchedule() {
        try {
            Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane()
                    .getScene().getWindow();

            Optional<AddScheduleController> controllerOpt = DialogUtils.showModalWithController("Add Schedule",
                    "/layouts/schedule/AddScheduleView.fxml", mainStage, ctrl -> {
                        ctrl.setCustomerManager(AppContext.getServiceFactory().getCustomerManager());
                        ctrl.setWasteManager(AppContext.getServiceFactory().getWasteManager());
                        ctrl.setOneTimeScheduleManager(oneTimeScheduleManager);
                        ctrl.setRecurringScheduleManager(recurringScheduleManager);
                        ctrl.initData();
                    });

            if (controllerOpt.isPresent()) {
                AddScheduleController ctrl = controllerOpt.get();
                loadSchedules();
            }
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Loading Error", "Could not load Add Schedule dialog.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the action to change the frequency of a recurring schedule.
     */
    @FXML
    private void handleChangeFrequency() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select a schedule to edit.",
                    AppContext.getOwner());
            return;
        }
        if (selected.getScheduleCategory() == ScheduleCategory.ONE_TIME) {
            DialogUtils.showError("Edit not allowed", "Cannot edit one-time schedules.",
                    AppContext.getOwner());
            return;
        }

        try {
            Optional<RecurringSchedule> scheduleOpt = recurringScheduleManager
                    .findRecurringScheduleById(selected.getId());
            if (scheduleOpt.isEmpty()) {
                DialogUtils.showError("Error", "Schedule not found.",
                        AppContext.getOwner());
                return;
            }
            RecurringSchedule schedule = scheduleOpt.get();

            Optional<ChangeFrequencyDialogController> controllerOpt = DialogUtils
                    .showModalWithController("Change Frequency",
                            "/layouts/schedule/ChangeFrequencyDialog.fxml",
                            (Stage) MainLayoutController.getInstance()
                                    .getRootPane().getScene().getWindow(),
                            ctrl -> {
                                ctrl.setSchedule(schedule);
                                ctrl.setFrequencies(List.of(Frequency.values()));
                                ctrl.setCurrentFrequency(schedule.getFrequency());
                            });

            if (controllerOpt.isPresent()) {
                Frequency newFreq = controllerOpt.get().getSelectedFrequency();
                if (newFreq != null) {
                    recurringScheduleManager.updateFrequency(schedule,
                            newFreq);
                    loadSchedules();
                }
            }
        } catch (IOException e) {
            DialogUtils.showError("Loading Error", "Failed to load the dialog.",
                    AppContext.getOwner());
        } catch (Exception e) {
            DialogUtils.showError("Unexpected Error", "An unexpected error occurred.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the action to delete (cancel) the selected schedule.
     */
    @FXML
    private void handleDeleteSchedule() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION,
                    "Please select a schedule to delete.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to cancel this schedule?",
                AppContext.getOwner());

        if (!confirmed) {
            return;
        }

        boolean success = false;

        try {
            if (selected.getScheduleCategory() == ScheduleCategory.ONE_TIME) {
                Optional<OneTimeSchedule> scheduleOpt = oneTimeScheduleManager
                        .findOneTimeScheduleById(selected.getId());
                if (scheduleOpt.isPresent()) {
                    OneTimeSchedule schedule = scheduleOpt.get();
                    success = oneTimeScheduleManager.softDeleteOneTimeSchedule(schedule);
                }
            } else if (selected.getScheduleCategory() == ScheduleCategory.RECURRING) {
                Optional<RecurringSchedule> scheduleOpt = recurringScheduleManager.findRecurringScheduleById(
                        selected.getId());

                if (scheduleOpt.isPresent()) {
                    RecurringSchedule schedule = scheduleOpt.get();
                    success = recurringScheduleManager.updateStatusRecurringSchedule(
                            schedule, ScheduleStatus.CANCELLED);
                }
            } else {
                DialogUtils.showError("Unknown Schedule Category",
                        "The selected schedule has an unknown category.",
                        AppContext.getOwner());
            }

            if (success) {
                DialogUtils.showSuccess("Schedule cancelled successfully.",
                        AppContext.getOwner());
                loadSchedules();
            } else {
                DialogUtils.showError("Cancellation failed",
                        "Schedule could not be cancelled.", AppContext.getOwner());
            }

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error",
                    "An error occurred while cancelling the schedule.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the search/filtering of schedules based on the search field and active filters.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            scheduleTable.setItems(FXCollections.observableArrayList(allSchedules));
            return;
        }

        ObservableList<ScheduleRow> filtered = FXCollections.observableArrayList();

        for (ScheduleRow row : allSchedules) {
            if ((activeFilters.contains(FILTER_WASTE_TYPE)
                    && row.getWasteName().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_FREQUENCY)
                            && row.getFrequency() != null
                            && row.getFrequency().name().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_CUSTOMER)
                            && row.getCustomer().toLowerCase().contains(query))) {
                filtered.add(row);
            }
        }

        scheduleTable.setItems(filtered);
    }

    /**
     * Handles the reset of the search field and filter checkboxes.
     */
    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FILTER_WASTE_TYPE, FILTER_FREQUENCY, FILTER_CUSTOMER);
        oneTimeCheckBox.setSelected(true);
        recurringCheckBox.setSelected(true);

        loadSchedules();
    }

    /**
     * Handles the action to show collections related to the selected schedule.
     */
    @FXML
    private void handleShowRelatedCollections() {
        ScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION,
                    "Please select a schedule to view associated collection.",
                    AppContext.getOwner());
            return;
        }

        Optional<? extends Schedule> scheduleOpt;
        if (selected.getScheduleCategory() == ScheduleCategory.ONE_TIME) {
            scheduleOpt = oneTimeScheduleManager.findOneTimeScheduleById(selected.getId());
        } else {
            scheduleOpt = recurringScheduleManager.findRecurringScheduleById(selected.getId());
        }

        if (scheduleOpt.isEmpty()) {
            DialogUtils.showError("Error", "Schedule not found.", AppContext.getOwner());
            return;
        }

        Schedule schedule = scheduleOpt.get();
        List<Collection> collections = collectionManager.getAllCollectionBySchedule(schedule);

        try {
            MainLayoutController.getInstance().setPageTitle("Associated Collections");
            CollectionController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/collection/CollectionView.fxml");
            controller.setPreviousPage("SCHEDULE");
            controller.setCollectionManager(collectionManager);
            controller.setScheduleManager(scheduleManager);
            controller.setOneTimeScheduleManager(oneTimeScheduleManager);
            controller.setRecurringScheduleManager(recurringScheduleManager);
            controller.setCollections(collections);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error",
                    "Could not load Associated Collections view.", AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Shows the filter menu for selecting which fields to search.
     *
     * @param event the mouse event triggering the menu
     */
    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { FILTER_WASTE_TYPE, FILTER_FREQUENCY, FILTER_CUSTOMER };
        String[] labels = { "Waste", "Frequency", "Customer" };

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected.booleanValue()) {
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
