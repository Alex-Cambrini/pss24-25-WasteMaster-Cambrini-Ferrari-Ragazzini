package it.unibo.wastemaster.infrastructure.di;

import it.unibo.wastemaster.domain.factory.CollectionFactory;
import it.unibo.wastemaster.domain.factory.CollectionFactoryImpl;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.repository.impl.AccountRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.CollectionRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.CustomerRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.EmployeeRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.InvoiceRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.OneTimeScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.RecurringScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.ScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.TripRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.VehicleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.WasteRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.WasteScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.service.AccountManager;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.EmployeeManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.LoginManager;
import it.unibo.wastemaster.domain.service.NotificationManager;
import it.unibo.wastemaster.domain.service.NotificationService;
import it.unibo.wastemaster.domain.service.OneTimeScheduleManager;
import it.unibo.wastemaster.domain.service.RecurringScheduleManager;
import it.unibo.wastemaster.domain.service.ScheduleManager;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.domain.service.WasteManager;
import it.unibo.wastemaster.domain.service.WasteScheduleManager;
import it.unibo.wastemaster.infrastructure.dao.AccountDAO;
import it.unibo.wastemaster.infrastructure.dao.CollectionDAO;
import it.unibo.wastemaster.infrastructure.dao.CustomerDAO;
import it.unibo.wastemaster.infrastructure.dao.EmployeeDAO;
import it.unibo.wastemaster.infrastructure.dao.GenericDAO;
import it.unibo.wastemaster.infrastructure.dao.InvoiceDAO;
import it.unibo.wastemaster.infrastructure.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.infrastructure.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.infrastructure.dao.ScheduleDAO;
import it.unibo.wastemaster.infrastructure.dao.TripDAO;
import it.unibo.wastemaster.infrastructure.dao.VehicleDAO;
import it.unibo.wastemaster.infrastructure.dao.WasteDAO;
import it.unibo.wastemaster.infrastructure.dao.WasteScheduleDAO;
import it.unibo.wastemaster.infrastructure.notification.FakeNotificationService;
import jakarta.persistence.EntityManager;

/**
 * Factory class responsible for creating and providing all domain services
 * and their dependencies. It initializes repositories and managers,
 * wiring them together to ensure proper service relationships.
 */
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
    private final LoginManager loginManager;
    private final NotificationManager notificationManager;
    private final NotificationService notificationService;
    private final CollectionFactory collectionFactory;

    /**
     * Constructs all services and their dependencies using the provided EntityManager.
     *
     * @param em the EntityManager used to instantiate DAOs
     */
    public ServiceFactory(final EntityManager em) {

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
        var recurringScheduleRepository =
                new RecurringScheduleRepositoryImpl(recurringScheduleDao);
        var collectionRepository = new CollectionRepositoryImpl(collectionDao);
        var oneTimeScheduleRepository =
                new OneTimeScheduleRepositoryImpl(oneTimeScheduleDao);
        var vehicleRepository = new VehicleRepositoryImpl(vehicleDao);
        var tripRepository = new TripRepositoryImpl(tripDao);
        var invoiceRepository = new InvoiceRepositoryImpl(invoiceDao);
        var scheduleRepository = new ScheduleRepositoryImpl(scheduleDao);

        this.collectionFactory = new CollectionFactoryImpl();
        this.accountManager = new AccountManager(accountRepository);
        this.loginManager = new LoginManager(accountRepository);
        this.wasteManager = new WasteManager(wasteRepository);
        this.customerManager = new CustomerManager(customerRepository);
        this.wasteScheduleManager = new WasteScheduleManager(wasteScheduleRepository);
        this.recurringScheduleManager =
                new RecurringScheduleManager(recurringScheduleRepository,
                        wasteScheduleManager);
        this.collectionManager =
                new CollectionManager(collectionRepository, recurringScheduleManager,
                        collectionFactory);
        this.recurringScheduleManager.setCollectionManager(collectionManager);
        this.oneTimeScheduleManager =
                new OneTimeScheduleManager(oneTimeScheduleRepository, collectionManager);
        this.vehicleManager = new VehicleManager(vehicleRepository);
        this.tripManager = new TripManager(tripRepository, collectionRepository,
                recurringScheduleManager);
        this.invoiceManager = new InvoiceManager(invoiceRepository);
        this.employeeManager = new EmployeeManager(employeeRepository, accountManager);
        this.scheduleManager = new ScheduleManager(scheduleRepository);
        this.notificationManager =
                new NotificationManager(tripRepository, invoiceRepository,
                        customerRepository);
        this.notificationService = new FakeNotificationService();

    }

    /**
     * @return the account manager
     */
    public AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * @return the login manager
     */
    public LoginManager getLoginManager() {
        return loginManager;
    }

    /**
     * @return the employee manager
     */
    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    /**
     * @return the waste manager
     */
    public WasteManager getWasteManager() {
        return wasteManager;
    }

    /**
     * @return the customer manager
     */
    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    /**
     * @return the waste schedule manager
     */
    public WasteScheduleManager getWasteScheduleManager() {
        return wasteScheduleManager;
    }

    /**
     * @return the recurring schedule manager
     */
    public RecurringScheduleManager getRecurringScheduleManager() {
        return recurringScheduleManager;
    }

    /**
     * @return the collection manager
     */
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * @return the one-time schedule manager
     */
    public OneTimeScheduleManager getOneTimeScheduleManager() {
        return oneTimeScheduleManager;
    }

    /**
     * @return the vehicle manager
     */
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    /**
     * @return the trip manager
     */
    public TripManager getTripManager() {
        return tripManager;
    }

    /**
     * @return the invoice manager
     */
    public InvoiceManager getInvoiceManager() {
        return invoiceManager;
    }

    /**
     * @return the schedule manager
     */
    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    /**
     * @return the notification manager
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /**
     * @return the notification service
     */
    public NotificationService getNotificationService() {
        return notificationService;
    }
}
