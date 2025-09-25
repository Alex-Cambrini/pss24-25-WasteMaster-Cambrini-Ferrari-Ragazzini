package it.unibo.wastemaster.controller.invoice;
import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.customer.CustomerDetailController;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class InvoiceController {

    private static final int REFRESH_INTERVAL_SECONDS = 30;
    private static final String FILTER_ID = "id";
    private static final String FILTER_CUSTOMER = "customer";
    private static final String FILTER_STATUS = "status";
    private static final String TITLE_NO_SELECTION = "No Selection";

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS
    );
    private final ObservableList<InvoiceRow> allInvoices = FXCollections.observableArrayList();
    private Stage owner;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private CustomerManager customerManager;

    private Timeline refreshTimeline;

    @FXML private Button markAsPaidButton;
    @FXML private Button addInvoiceButton;
    @FXML private Button editInvoiceButton;
    @FXML private Button deleteInvoiceButton;
    @FXML private Button viewCollectionButton;
    @FXML private ContextMenu filterMenu;
    @FXML private TableView<InvoiceRow> invoiceTable;
    @FXML private TableView<CustomerRow> customerTable;
    @FXML private TableColumn<InvoiceRow, String> idColumn;
    @FXML private TableColumn<InvoiceRow, String> customerColumn;
    @FXML private TableColumn<InvoiceRow, String> amountColumn;
    @FXML private TableColumn<InvoiceRow, String> statusColumn;
    @FXML private TableColumn<InvoiceRow, String> dateColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;


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
        owner = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

        idColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_ID));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_CUSTOMER));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_STATUS));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        invoiceTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> updateButtons(newVal));

        statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "PAID", "UNPAID"));
        statusFilterCombo.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> handleSearch());
    }

    public void initData() {
        if (invoiceManager == null || collectionManager == null || customerManager == null) {
            throw new IllegalStateException("Managers must be set before calling initData");
        }

        loadInvoices();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS),
                e -> loadInvoices()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadInvoices() {
        allInvoices.clear();
        List<Invoice> invoices = invoiceManager.getAllInvoices();
        for (Invoice invoice : invoices) {
            allInvoices.add(new InvoiceRow(invoice));
        }
        invoiceTable.setItems(FXCollections.observableArrayList(allInvoices));
    }

    private void updateButtons(final InvoiceRow selected) {
    boolean rowSelected = selected != null;
    editInvoiceButton.setDisable(!rowSelected);
    deleteInvoiceButton.setDisable(!rowSelected);
    viewCollectionButton.setDisable(!rowSelected);
     markAsPaidButton.setDisable(!rowSelected || 
    (selected != null && selected.getStatus().equalsIgnoreCase("PAID")));

    }

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
            DialogUtils.showError("Loading Error", "Could not load Add Invoice dialog.", AppContext.getOwner());
        }
    }

    

    @FXML
    private void handleSearch() {
    String query = searchField.getText().toLowerCase().trim();
    String statusFilter = statusFilterCombo.getValue();

    if (query.isEmpty()) {
        ObservableList<InvoiceRow> filtered = FXCollections.observableArrayList(allInvoices);
        
        if (!"ALL".equals(statusFilter)) {
            filtered.removeIf(row -> !row.getStatus().equalsIgnoreCase(statusFilter));
        }
        invoiceTable.setItems(filtered);
        return;
    }
    ObservableList<InvoiceRow> filtered = FXCollections.observableArrayList();
    for (InvoiceRow row : allInvoices) {
        if ((activeFilters.contains(FILTER_ID) && row.getId().toLowerCase().contains(query))
                || (activeFilters.contains(FILTER_CUSTOMER) && row.getCustomer().toLowerCase().contains(query))
                || (activeFilters.contains(FILTER_STATUS) && row.getStatus().toLowerCase().contains(query))) {
            filtered.add(row);
        }
    }
    
    if (!"ALL".equals(statusFilter)) {
        filtered.removeIf(row -> !row.getStatus().equalsIgnoreCase(statusFilter));
    }
    invoiceTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FILTER_ID, FILTER_CUSTOMER, FILTER_STATUS);
        loadInvoices();
    }

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

    @FXML
    private void handleViewCollection() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice.", AppContext.getOwner());
            return;
        }
        Optional<Invoice> invoiceOpt = invoiceManager.findInvoiceById(selected.getIdAsInt());
        if (invoiceOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Invoice not found.", AppContext.getOwner());
            return;
        }
        List<Collection> collections = invoiceOpt.get().getCollections();
        if (collections == null || collections.isEmpty()) {
            DialogUtils.showError("No Collection", "No collections associated with this invoice.", AppContext.getOwner());
            return;
        }
        try {
            MainLayoutController.getInstance().setPageTitle("Associated Collections");
            it.unibo.wastemaster.controller.collection.CollectionController controller =
                    MainLayoutController.getInstance().loadCenterWithController("/layouts/collection/CollectionView.fxml");
            controller.setCollections(collections);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Collection view.", AppContext.getOwner());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleMarkAsPaid() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice to mark as paid.", AppContext.getOwner());
            return;
        }
        boolean success = invoiceManager.markInvoiceAsPaid(selected.getIdAsInt());
        if (success) {
            DialogUtils.showSuccess("Invoice marked as paid.", AppContext.getOwner());
            loadInvoices();
        } else {
            DialogUtils.showError("Error", "Could not mark invoice as paid.", AppContext.getOwner());
        }
    }

//    @FXML
//    private void handleViewCustomerDetail() {
//        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice.", AppContext.getOwner());
//            return;
//        }
//        Customer customer = selected.getCustomerObject();
//        CustomerDetailController controller = MainLayoutController.getInstance()
//            .loadCenterWithController("/layouts/customer/CustomerDetailView.fxml");
//        controller.setManagers(collectionManager, invoiceManager);
//        controller.setCustomer(customer);
//    }

    @FXML
    private void handleDeleteInvoice() {
        InvoiceRow selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError(TITLE_NO_SELECTION, "Please select an invoice to delete.", AppContext.getOwner());
            return;
        }
        boolean confirmed = DialogUtils.showConfirmationDialog("Confirm Deletion",
                "Are you sure you want to delete invoice ID " + selected.getId() + "?", AppContext.getOwner());
        if (!confirmed) {
            return;
        }
        boolean success = invoiceManager.cancelInvoice(selected.getIdAsInt());
        if (success) {
            DialogUtils.showSuccess("Invoice deleted successfully.", AppContext.getOwner());
            loadInvoices();
        } else {
            DialogUtils.showError("Error", "Could not delete invoice.", AppContext.getOwner());
        }
    }


}
