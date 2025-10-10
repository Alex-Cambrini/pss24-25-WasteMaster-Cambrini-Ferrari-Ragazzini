package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.customerstatistics.CustomerStatisticsController;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.presentationdto.CustomerRow;
import it.unibo.wastemaster.presentationdto.InvoiceRow;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for managing the invoices view, including filtering, searching,
 * marking as paid, exporting, and viewing associated customers and collections.
 */
public final class InvoiceController implements AutoRefreshable {

    private static final int REFRESH_INTERVAL_SECONDS = 30;

    private static final String FILTER_ID = "id";
    private static final String FILTER_CUSTOMER = "customer";
    private static final String FILTER_STATUS = "status";
    private static final String FILTER_AMOUNT = "amount";

    private static final String TITLE_NO_SELECTION = "No Selection";

    private final ObservableList<String> activeFilters =
            FXCollections.observableArrayList(
                    FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS);
    private final ObservableList<InvoiceRow> allInvoices =
            FXCollections.observableArrayList();
    private Stage owner;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private CustomerManager customerManager;

    private Timeline refreshTimeline;

    @FXML
    private Button markAsPaidButton;

    @FXML
    private Button addInvoiceButton;

    @FXML
    private Button deleteInvoiceButton;

    @FXML
    private ContextMenu filterMenu;

    @FXML
    private TableView<InvoiceRow> invoiceTable;

    @FXML
    private TableView<CustomerRow> customerTable;

    @FXML
    private TableColumn<InvoiceRow, String> idColumn;

    @FXML
    private TableColumn<InvoiceRow, String> customerColumn;

    @FXML
    private TableColumn<InvoiceRow, String> invoiceAmountColumn;

    @FXML
    private TableColumn<InvoiceRow, String> paymentDateColumn;

    @FXML
    private TableColumn<InvoiceRow, String> statusColumn;

    @FXML
    private TableColumn<InvoiceRow, String> dateColumn;

    @FXML
    private TableColumn<InvoiceRow, String> serviceCountsColumn;

    @FXML
    private TableColumn<InvoiceRow, String> totalAmountsColumn;

    @FXML
    private TableColumn<InvoiceRow, String> isCancelledColumn;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox showDeletedCheckBox;

    @FXML
    private CheckBox showPaidCheckBox;

    @FXML
    private CheckBox showNotPaidCheckBox;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button viewCustomerButton;

    /**
     * Sets the invoice manager used for invoice operations.
     *
     * @param invoiceManager the InvoiceManager to use
     */
    public void setInvoiceManager(final InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    /**
     * Sets the collection manager used for collection operations.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(final CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the customer manager used for customer operations.
     *
     * @param customerManager the CustomerManager to use
     */
    public void setCustomerManager(final CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Initializes the invoice view, table columns, filters, and listeners.
     */
    @FXML
    public void initialize() {
        owner = (Stage) MainLayoutController.getInstance().getRootPane().getScene()
                .getWindow();

        DateTimeFormatter tsFtm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        invoiceAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        serviceCountsColumn.setCellValueFactory(
                new PropertyValueFactory<>("serviceCounts"));
        totalAmountsColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalAmounts"));
        isCancelledColumn.setCellValueFactory(new PropertyValueFactory<>("isCancelled"));

        java.util.function.Function<String, String> fmt = s -> {
            if (s == null || s.isBlank()) {
                return "";
            }
            try {
                return java.time.LocalDateTime.parse(s).format(tsFtm);
            } catch (Exception e1) {
                try {
                    return java.time.OffsetDateTime.parse(s).toLocalDateTime()
                            .format(tsFtm);
                } catch (Exception e2) {
                    try {
                        return java.time.ZonedDateTime.parse(s).toLocalDateTime()
                                .format(tsFtm);
                    } catch (Exception e3) {
                        return s;
                    }
                }
            }
        };

        dateColumn.setCellFactory(
                col -> new javafx.scene.control.TableCell<InvoiceRow, String>() {
                    @Override
                    protected void updateItem(final String item, final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : fmt.apply(item));
                    }
                });

        paymentDateColumn.setCellFactory(
                col -> new javafx.scene.control.TableCell<InvoiceRow, String>() {
                    @Override
                    protected void updateItem(final String item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText((item == null || item.isBlank()) ? "-"
                                    : fmt.apply(item));
                        }
                    }
                });

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        invoiceTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    updateButtons(newVal);
                    viewCustomerButton.setDisable(newVal == null);
                });

        if (showDeletedCheckBox != null) {
            showDeletedCheckBox.setSelected(false);
            showDeletedCheckBox.selectedProperty()
                    .addListener((obs, o, n) -> handleSearch());
        }
        if (showPaidCheckBox != null) {
            showPaidCheckBox.setSelected(true);
            showPaidCheckBox.selectedProperty()
                    .addListener((obs, o, n) -> handleSearch());
        }
        if (showNotPaidCheckBox != null) {
            showNotPaidCheckBox.setSelected(true);
            showNotPaidCheckBox.selectedProperty()
                    .addListener((obs, o, n) -> handleSearch());
        }

        invoiceTable.setRowFactory(tv -> new TableRow<InvoiceRow>() {
            @Override
            protected void updateItem(final InvoiceRow item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    boolean cancelled = "Yes".equalsIgnoreCase(item.getIsCancelled());
                    if ("UNPAID".equalsIgnoreCase(item.getStatus()) && !cancelled) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Loads initial invoice data into the table.
     * Must be called after all managers are set.
     */
    public void initData() {
        if (invoiceManager == null || collectionManager == null
                || customerManager == null) {
            throw new IllegalStateException(
                    "Managers must be set before calling initData");
        }
        loadInvoices();
    }

    /**
     * Starts the automatic refresh of the invoice table.
     */
    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null) {
            return;
        }
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS),
                        e -> loadInvoices()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh of the invoice table.
     */
    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Loads all invoices from the database and updates the invoice table.
     */
    private void loadInvoices() {
        if (AppContext.getCurrentAccount() == null) {
            return;
        }
        allInvoices.clear();
        List<Invoice> invoices = invoiceManager.getAllInvoices();
        for (Invoice invoice : invoices) {
            allInvoices.add(new InvoiceRow(invoice));
        }
        handleSearch();
    }

    /**
     * Updates the state of action buttons based on the selected invoice.
     *
     * @param selected the selected InvoiceRow
     */
    private void updateButtons(final InvoiceRow selected) {
        if (selected == null) {
            markAsPaidButton.setDisable(true);
            deleteInvoiceButton.setDisable(true);
            exportPdfButton.setDisable(true);
            return;
        }

        boolean isPaid = "PAID".equalsIgnoreCase(selected.getStatus());
        boolean isCancelled = "Yes".equalsIgnoreCase(selected.getIsCancelled());

        markAsPaidButton.setDisable(isPaid || isCancelled);
        deleteInvoiceButton.setDisable(isPaid || isCancelled);
        exportPdfButton.setDisable(isCancelled);
    }

    /**
     * Handles the action to add a new invoice.
     */
    @FXML
    private void handleAddInvoice() {
        try {
            Optional<AddInvoiceController> controllerOpt =
                    DialogUtils.showModalWithController("Add Invoice",
                            "/layouts/invoice/AddInvoiceView.fxml", owner, ctrl -> {
                                ctrl.setCustomerManager(customerManager);
                                ctrl.setCollectionManager(collectionManager);
                                ctrl.setInvoiceManager(invoiceManager);
                                ctrl.initData();
                            });

            if (controllerOpt.isPresent()) {
                loadInvoices();
            }
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Loading Error", "Could not load Add Invoice dialog.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the search/filtering of invoices based on the search field and filter
     * checkboxes.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        boolean showDeleted =
                showDeletedCheckBox != null && showDeletedCheckBox.isSelected();
        boolean showPaid = showPaidCheckBox == null || showPaidCheckBox.isSelected();
        boolean showUnpaid =
                showNotPaidCheckBox == null || showNotPaidCheckBox.isSelected();

        boolean anyStatusSelected = showPaid || showUnpaid;

        ObservableList<InvoiceRow> filtered = FXCollections.observableArrayList();

        for (InvoiceRow row : allInvoices) {
            boolean matchesQuery = query.isEmpty()
                    || (activeFilters.contains(FILTER_ID) && row.getId().toLowerCase()
                    .contains(query))
                    || (activeFilters.contains(FILTER_CUSTOMER) && row.getCustomer()
                    .toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_STATUS) && row.getStatus()
                    .toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_AMOUNT) && row.getAmount()
                    .toLowerCase().contains(query));

            boolean isCancelled = "Yes".equalsIgnoreCase(row.getIsCancelled());
            String status = row.getStatus();

            boolean matchesCancelled = showDeleted == isCancelled;

            boolean matchesStatus =
                    (showPaid && "PAID".equalsIgnoreCase(status)) ||
                            (showUnpaid && "UNPAID".equalsIgnoreCase(status));

            if (matchesQuery && matchesCancelled && matchesStatus) {
                filtered.add(row);
            }
        }

        invoiceTable.setItems(filtered);
    }

    /**
     * Handles the reset of the search field and filter checkboxes.
     */
    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS);

        if (showDeletedCheckBox != null) {
            showDeletedCheckBox.setSelected(false);
        }
        if (showPaidCheckBox != null) {
            showPaidCheckBox.setSelected(true);
        }
        if (showNotPaidCheckBox != null) {
            showNotPaidCheckBox.setSelected(true);
        }

        loadInvoices();
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
        String[] fields = {FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS};
        String[] labels = {"ID", "Customer", "Status"};
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
        filterMenu.show(invoiceTable, event.getScreenX(), event.getScreenY());
    }

    /**
     * Handles the action to view the collections associated with the selected invoice.
     */
    @FXML
    private void handleViewCollection() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice.",
                    AppContext.getOwner());
            return;
        }
        Optional<Invoice> invoiceOpt =
                invoiceManager.findInvoiceById(selected.getInvoice()
                        .getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Invoice not found.",
                    AppContext.getOwner());
            return;
        }
        List<Collection> collections = invoiceOpt.get().getCollections();
        if (collections == null || collections.isEmpty()) {
            DialogUtils.showError("No Collection",
                    "No collections associated with this invoice.",
                    AppContext.getOwner());
            return;
        }
        try {
            MainLayoutController.getInstance().setPageTitle("Associated Collections");
            it.unibo.wastemaster.controller.collection.CollectionController controller =
                    MainLayoutController
                            .getInstance().loadCenterWithController(
                                    "/layouts/collection/CollectionView.fxml");
            controller.setCollections(collections);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Collection view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action to mark the selected invoice as paid.
     */
    @FXML
    private void handleMarkAsPaid() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION,
                    "Please select an invoice to mark as paid.", AppContext.getOwner());
            return;
        }
        boolean success = invoiceManager.markInvoiceAsPaid(selected.getInvoice()
                .getInvoiceId());
        if (success) {
            DialogUtils.showSuccess("Invoice marked as paid.", AppContext.getOwner());
            loadInvoices();
        } else {
            DialogUtils.showError("Error", "Could not mark invoice as paid.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the action to export the selected invoice as a PDF file.
     */
    @FXML
    private void handleExportPdf() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select an invoice to export.",
                    AppContext.getOwner());
            return;
        }

        Optional<Invoice> invoiceOpt =
                invoiceManager.findInvoiceById(selected.getInvoice().getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Invoice not found.",
                    AppContext.getOwner());
            return;
        }

        Invoice invoice = invoiceOpt.get();
        java.nio.file.Path outputPath =
                java.nio.file.Paths.get("invoice-" + invoice.getInvoiceId() + ".pdf");
        java.io.File file = outputPath.toFile();

        if (file.exists() && !file.renameTo(file)) {
            DialogUtils.showError("File in use",
                    "The PDF is already open in another program."
                            + " Close it and try again.",
                    AppContext.getOwner());
            return;
        }

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            new it.unibo.wastemaster.infrastructure.pdf.InvoicePdfService()
                    .generateInvoicePdf(
                            invoice, fos);
            DialogUtils.showSuccess("PDF generated: " + outputPath.toAbsolutePath(),
                    AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Export Error", "Could not generate PDF.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the action to delete the selected invoice.
     */
    @FXML
    private void handleDeleteInvoice() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION,
                    "Please select an invoice to delete.", AppContext.getOwner());
            return;
        }
        boolean confirmed = DialogUtils.showConfirmationDialog("Confirm Deletion",
                "Are you sure you want to delete invoice ID " + selected.getId() + "?",
                AppContext.getOwner());
        if (!confirmed) {
            return;
        }
        boolean success = invoiceManager.deleteInvoice(selected.getInvoice()
                .getInvoiceId());
        if (success) {
            DialogUtils.showSuccess("Invoice deleted successfully.",
                    AppContext.getOwner());
            loadInvoices();
        } else {
            DialogUtils.showError("Error", "Could not delete invoice.",
                    AppContext.getOwner());
        }
    }

    /**
     * Handles the action to view statistics for the customer associated with the
     * selected invoice.
     *
     * @param event the action event
     */
    @FXML
    private void handleViewCustomer(final ActionEvent event) {
        InvoiceRow selectedRow = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            DialogUtils.showError("No Selection", "Please select an invoice.",
                    AppContext.getOwner());
            return;
        }
        Customer customer = selectedRow.getInvoice().getCustomer();

        try {
            Optional<CustomerStatisticsController> controllerOpt =
                    DialogUtils.showModalWithController(
                            "Customer Statistics",
                            "/layouts/customerstatistics/CustomerStatisticsView.fxml",
                            AppContext.getOwner(),
                            ctrl -> {
                                ctrl.setCustomerManager(customerManager);
                                ctrl.setInvoiceManager(invoiceManager);
                                ctrl.setCollectionManager(collectionManager);
                                ctrl.setCustomer(customer);
                            });

        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showError("Loading Error",
                    "Could not load Customer Statistics dialog.", AppContext.getOwner());
        }
    }
}
