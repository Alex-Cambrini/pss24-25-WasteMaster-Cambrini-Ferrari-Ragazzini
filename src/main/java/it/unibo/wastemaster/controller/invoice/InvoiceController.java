package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.viewmodels.CustomerRow;
import it.unibo.wastemaster.viewmodels.InvoiceRow;
import java.io.IOException;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class InvoiceController implements AutoRefreshable {

    private static final int REFRESH_INTERVAL_SECONDS = 30;

    private static final String FILTER_ID = "id";
    private static final String FILTER_CUSTOMER = "customer";
    private static final String FILTER_STATUS = "status";
    private static final String FILTER_AMOUNT = "amount";

    private static final String TITLE_NO_SELECTION = "No Selection";

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS);
    private final ObservableList<InvoiceRow> allInvoices = FXCollections.observableArrayList();
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

    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @FXML
    public void initialize() {
        owner = (Stage) MainLayoutController.getInstance().getRootPane().getScene()
                .getWindow();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        invoiceAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        serviceCountsColumn.setCellValueFactory(
                new PropertyValueFactory<>("serviceCounts"));
        totalAmountsColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalAmounts"));
        isCancelledColumn.setCellValueFactory(new PropertyValueFactory<>("isCancelled"));

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        invoiceTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> updateButtons(newVal));

        if (showDeletedCheckBox != null) {
            showDeletedCheckBox.setSelected(false);
            showDeletedCheckBox.selectedProperty().addListener((obs, o, n) -> handleSearch());
        }
        if (showPaidCheckBox != null) {
            showPaidCheckBox.setSelected(true);
            showPaidCheckBox.selectedProperty().addListener((obs, o, n) -> handleSearch());
        }
        if (showNotPaidCheckBox != null) {
            showNotPaidCheckBox.setSelected(true);
            showNotPaidCheckBox.selectedProperty().addListener((obs, o, n) -> handleSearch());
        }

        invoiceTable.setRowFactory(tv -> new TableRow<InvoiceRow>() {
            @Override
            protected void updateItem(InvoiceRow item, boolean empty) {
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

    public void initData() {
        if (invoiceManager == null || collectionManager == null
                || customerManager == null) {
            throw new IllegalStateException(
                    "Managers must be set before calling initData");
        }
        loadInvoices();
    }

    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null)
            return;
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS), e -> loadInvoices()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    private void loadInvoices() {
        if (AppContext.getCurrentAccount() == null)
            return;
        allInvoices.clear();
        List<Invoice> invoices = invoiceManager.getAllInvoices();
        for (Invoice invoice : invoices) {
            allInvoices.add(new InvoiceRow(invoice));
        }
        handleSearch();
    }

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

    @FXML
    private void handleAddInvoice() {
        try {
            Optional<AddInvoiceController> controllerOpt = DialogUtils.showModalWithController("Add Invoice",
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

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        boolean showDeleted = showDeletedCheckBox != null && showDeletedCheckBox.isSelected();
        boolean showPaid = showPaidCheckBox == null || showPaidCheckBox.isSelected(); // default true se null
        boolean showUnpaid = showNotPaidCheckBox == null || showNotPaidCheckBox.isSelected(); // default true se null

        ObservableList<InvoiceRow> filtered = FXCollections.observableArrayList();

        for (InvoiceRow row : allInvoices) {
            boolean matchesQuery = query.isEmpty()
                    || (activeFilters.contains(FILTER_ID) && row.getId().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_CUSTOMER) && row.getCustomer().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_STATUS) && row.getStatus().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_AMOUNT) && row.getAmount().toLowerCase().contains(query));

            boolean isCancelled = "Yes".equalsIgnoreCase(row.getIsCancelled());
            boolean matchesDeleted = showDeleted || !isCancelled;

            String status = row.getStatus();
            boolean allowedByCheckbox = ("PAID".equalsIgnoreCase(status) && showPaid)
                    || ("UNPAID".equalsIgnoreCase(status) && showUnpaid);

            if (matchesQuery && matchesDeleted && allowedByCheckbox) {
                filtered.add(row);
            }
        }

        invoiceTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS);

        if (showDeletedCheckBox != null)
            showDeletedCheckBox.setSelected(false);
        if (showPaidCheckBox != null)
            showPaidCheckBox.setSelected(true);
        if (showNotPaidCheckBox != null)
            showNotPaidCheckBox.setSelected(true);

        loadInvoices();
    }

    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }
        filterMenu = new ContextMenu();
        String[] fields = { FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS };
        String[] labels = { "ID", "Customer", "Status" };
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

    @FXML
    private void handleViewCollection() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice.",
                    AppContext.getOwner());
            return;
        }
        Optional<Invoice> invoiceOpt = invoiceManager.findInvoiceById(selected.getInvoice()
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
            it.unibo.wastemaster.controller.collection.CollectionController controller = MainLayoutController
                    .getInstance().loadCenterWithController(
                            "/layouts/collection/CollectionView.fxml");
            controller.setCollections(collections);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Collection view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

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

    @FXML
    private void handleExportPdf() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select an invoice to export.", AppContext.getOwner());
            return;
        }

        Optional<Invoice> invoiceOpt = invoiceManager.findInvoiceById(selected.getInvoice().getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Invoice not found.", AppContext.getOwner());
            return;
        }

        Invoice invoice = invoiceOpt.get();
        java.nio.file.Path outputPath = java.nio.file.Paths.get("invoice-" + invoice.getInvoiceId() + ".pdf");
        java.io.File file = outputPath.toFile();

        if (file.exists() && !file.renameTo(file)) {
            DialogUtils.showError("File in use", "The PDF is already open in another program. Close it and try again.", AppContext.getOwner());
            return;
        }

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            new it.unibo.wastemaster.infrastructure.pdf.InvoicePdfService().generateInvoicePdf(invoice, fos);
            DialogUtils.showSuccess("PDF generated: " + outputPath.toAbsolutePath(), AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Export Error", "Could not generate PDF.", AppContext.getOwner());
        }
    }


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
}
