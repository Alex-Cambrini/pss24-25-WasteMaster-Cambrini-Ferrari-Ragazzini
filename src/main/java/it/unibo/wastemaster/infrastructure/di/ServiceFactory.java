package it.unibo.wastemaster.infrastructure.di;

import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.repository.impl.*;
import it.unibo.wastemaster.domain.service.*;
import it.unibo.wastemaster.infrastructure.dao.*;
import jakarta.persistence.EntityManager;

public class ServiceFactory {

    private final AccountManager accountManager;
    private final EmployeeManager employeeManager;
    private final WasteManager wasteManager;
    private final CustomerManager customerManager;
    private final WasteScheduleManager wasteScheduleManager;
    private final RecurringScheduleManager recurringScheduleManager;
    private final CollectionManager collectionManager;
    private final OneTimeScheduleManager oneTimeScheduleManager;
    private final VehicleManager vehicleManager;
    private final TripManager tripManager;
    private final InvoiceManager invoiceManager;
    private final ScheduleManager scheduleManager;

    public ServiceFactory(EntityManager em) {
        var locationDao = new GenericDAO<>(em, Location.class);
        var accountDao = new AccountDAO(em);
        var employeeDao = new EmployeeDAO(em);
        var wasteDao = new WasteDAO(em);
        var customerDao = new CustomerDAO(em);
        var wasteScheduleDao = new WasteScheduleDAO(em);
        var recurringScheduleDao = new RecurringScheduleDAO(em);
        var collectionDao = new CollectionDAO(em);
        var oneTimeScheduleDao = new OneTimeScheduleDAO(em);
        var vehicleDao = new VehicleDAO(em);
        var tripDao = new TripDAO(em);
        var invoiceDao = new InvoiceDAO(em);
        var scheduleDao = new ScheduleDAO(em);

        var accountRepository = new AccountRepositoryImpl(accountDao);
        var employeeRepository = new EmployeeRepositoryImpl(employeeDao);
        var wasteRepository = new WasteRepositoryImpl(wasteDao);
        var customerRepository = new CustomerRepositoryImpl(customerDao);
        var wasteScheduleRepository = new WasteScheduleRepositoryImpl(wasteScheduleDao);
        var recurringScheduleRepository = new RecurringScheduleRepositoryImpl(recurringScheduleDao);
        var collectionRepository = new CollectionRepositoryImpl(collectionDao);
        var oneTimeScheduleRepository = new OneTimeScheduleRepositoryImpl(oneTimeScheduleDao);
        var vehicleRepository = new VehicleRepositoryImpl(vehicleDao);
        var tripRepository = new TripRepositoryImpl(tripDao);
        var invoiceRepository = new InvoiceRepositoryImpl(invoiceDao);
        var scheduleRepository = new ScheduleRepositoryImpl(scheduleDao);

        this.accountManager = new AccountManager(accountRepository);
        this.wasteManager = new WasteManager(wasteRepository);
        this.customerManager = new CustomerManager(customerRepository);
        this.wasteScheduleManager = new WasteScheduleManager(wasteScheduleRepository);
        this.recurringScheduleManager = new RecurringScheduleManager(recurringScheduleRepository, wasteScheduleManager);
        this.collectionManager = new CollectionManager(collectionRepository, recurringScheduleManager);
        this.recurringScheduleManager.setCollectionManager(collectionManager);
        this.oneTimeScheduleManager = new OneTimeScheduleManager(oneTimeScheduleRepository, collectionManager);
        this.vehicleManager = new VehicleManager(vehicleRepository);
        this.tripManager = new TripManager(tripRepository);
        this.invoiceManager = new InvoiceManager(invoiceRepository, collectionRepository);
        this.employeeManager = new EmployeeManager(employeeRepository, accountManager);
        this.scheduleManager = new ScheduleManager(scheduleRepository);
    }

    public AccountManager getAccountManager() { return accountManager; }
    public EmployeeManager getEmployeeManager() { return employeeManager; }
    public WasteManager getWasteManager() { return wasteManager; }
    public CustomerManager getCustomerManager() { return customerManager; }
    public WasteScheduleManager getWasteScheduleManager() { return wasteScheduleManager; }
    public RecurringScheduleManager getRecurringScheduleManager() { return recurringScheduleManager; }
    public CollectionManager getCollectionManager() { return collectionManager; }
    public OneTimeScheduleManager getOneTimeScheduleManager() { return oneTimeScheduleManager; }
    public VehicleManager getVehicleManager() { return vehicleManager; }
    public TripManager getTripManager() { return tripManager; }
    public InvoiceManager getInvoiceManager() { return invoiceManager; }
    public ScheduleManager getScheduleManager() {return scheduleManager;}
}
