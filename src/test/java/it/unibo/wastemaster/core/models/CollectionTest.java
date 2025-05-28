package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectionTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste plastic;
    private Collection collection;
    private OneTimeSchedule schedule;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890");

        date = LocalDate.now();
        plastic = new Waste("PLASTICA", true, false);

        schedule = new OneTimeSchedule(customer, plastic, date);
        collection = new Collection(schedule);
    }

    @Test
    void testCollectionGettersAndSetters() {
        final int cancelLimitDays = 5;
        collection = new Collection(schedule);
        Waste glass = new Waste("VETRO", true, false);
        collection.setCollectionDate(date);
        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
        collection.setCancelLimitDays(cancelLimitDays);
        collection.setWaste(glass);

        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getCollectionDate());
        assertEquals(glass, collection.getWaste());
        assertEquals(Collection.CollectionStatus.COMPLETED,
                collection.getCollectionStatus());
        assertEquals(cancelLimitDays, collection.getCancelLimitDays());
        assertEquals(schedule, collection.getSchedule());
    }

    @Test
    void testToString() {
        String toStringOutput = collection.toString();

        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("Collection"));
        assertTrue(toStringOutput.contains("ID: " + collection.getCollectionId()));
        assertTrue(toStringOutput.contains(collection.getCustomer().getName()));
        assertTrue(toStringOutput.contains(collection.getCollectionDate().toString()));
        assertTrue(toStringOutput.contains(collection.getWaste().getWasteName()));
        assertTrue(toStringOutput.contains(collection.getCollectionStatus().name()));
        assertTrue(toStringOutput.contains(String.valueOf(Collection.CANCEL_LIMIT_DAYS)));
        assertTrue(toStringOutput.contains(String.valueOf(schedule.getScheduleId())));
        assertTrue(toStringOutput.contains(schedule.getScheduleCategory().name()));
    }

    @Test
    void testPersistence() {
        getEntityManager().getTransaction().begin();
        getCustomerDAO().insert(customer);
        getWasteDAO().insert(plastic);
        getOneTimeScheduleDAO().insert(schedule);
        getCollectionDAO().insert(collection);
        Collection found = getCollectionDAO().findById(collection.getCollectionId());
        assertNotNull(found);
        assertEquals(customer.getName(), found.getCustomer().getName());

        int foundId = found.getCollectionId();
        getCollectionDAO().delete(collection);

        Collection deleted = getCollectionDAO().findById(foundId);
        assertNull(deleted);
    }

    @Test
    void testCollectionValidation() {
        schedule = new OneTimeSchedule(null, null, null);
        Collection invalid = new Collection(null);
        invalid.setCollectionDate(date.minusDays(1));
        invalid.setCancelLimitDays(-1);
        invalid.setCollectionStatus(null);

        Set<ConstraintViolation<Collection>> violations =
                ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("The date must be today or in the future")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("The customer cannot be null")));
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("Cancellation days must be >= 0")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Schedule cannot be null")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("The waste type cannot be null")));
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("The collection status cannot be null")));
    }
}
