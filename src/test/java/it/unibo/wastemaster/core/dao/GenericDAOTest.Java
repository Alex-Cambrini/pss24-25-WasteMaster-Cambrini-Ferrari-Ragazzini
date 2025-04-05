import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Collection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GenericDAOTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private GenericDAO<Collection> collectionDAO;
    private GenericDAO<Customer> customerDAO;

    @BeforeEach
    public void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test-pu");
        entityManager = entityManagerFactory.createEntityManager();

        collectionDAO = new GenericDAO<>(entityManager, Collection.class);
        customerDAO = new GenericDAO<>(entityManager, Customer.class);
    }

    @Test
    public void testInsert() {
        Customer customer = new Customer("John Doe", "john.doe@example.com");
        Collection collection = new Collection(customer, null, null, Collection.CollectionStatus.PENDING, 7, 1, Collection.ScheduleType.SCHEDULED);

        entityManager.getTransaction().begin();
        customerDAO.insert(customer);
        collectionDAO.insert(collection);
        entityManager.getTransaction().commit();

        Collection retrievedCollection = collectionDAO.findById(collection.getCollectionId());
        assertNotNull(retrievedCollection, "Collection should be inserted into the database");
        assertEquals(collection.getCollectionId(), retrievedCollection.getCollectionId(), "Collection ID should match");
    }

    @Test
    public void testFindAll() {
        Customer customer = new Customer("Jane Smith", "jane.smith@example.com");
        Collection collection1 = new Collection(customer, null, null, Collection.CollectionStatus.PENDING, 7, 2, Collection.ScheduleType.SCHEDULED);
        Collection collection2 = new Collection(customer, null, null, Collection.CollectionStatus.PENDING, 7, 3, Collection.ScheduleType.EXTRA);

        entityManager.getTransaction().begin();
        customerDAO.insert(customer);
        collectionDAO.insert(collection1);
        collectionDAO.insert(collection2);
        entityManager.getTransaction().commit();

        List<Collection> collections = collectionDAO.findAll();
        assertNotNull(collections, "Collections list should not be null");
        assertTrue(collections.size() >= 2, "There should be at least two collections in the database");
    }

    @Test
    public void testUpdate() {
        Customer customer = new Customer("Alice", "alice@example.com");
        Collection collection = new Collection(customer, null, null, Collection.CollectionStatus.PENDING, 7, 4, Collection.ScheduleType.SCHEDULED);

        entityManager.getTransaction().begin();
        customerDAO.insert(customer);
        collectionDAO.insert(collection);
        entityManager.getTransaction().commit();

        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);

        entityManager.getTransaction().begin();
        collectionDAO.update(collection);
        entityManager.getTransaction().commit();

        Collection updatedCollection = collectionDAO.findById(collection.getCollectionId());
        assertEquals(Collection.CollectionStatus.COMPLETED, updatedCollection.getCollectionStatus(), "Collection status should be updated");
    }

    @Test
    public void testDelete() {
        Customer customer = new Customer("Bob", "bob@example.com");
        Collection collection = new Collection(customer, null, null, Collection.CollectionStatus.PENDING, 7, 5, Collection.ScheduleType.SCHEDULED);

        entityManager.getTransaction().begin();
        customerDAO.insert(customer);
        collectionDAO.insert(collection);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        collectionDAO.delete(collection);
        entityManager.getTransaction().commit();

        Collection deletedCollection = collectionDAO.findById(collection.getCollectionId());
        assertNull(deletedCollection, "Collection should be deleted from the database");
    }

    @AfterEach
    public void tearDown() {

        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
