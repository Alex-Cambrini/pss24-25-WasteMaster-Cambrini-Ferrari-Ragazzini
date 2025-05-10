package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

class CollectionTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste.WasteType wasteType;
    private Collection collection;
    private OneTimeSchedule schedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");

        date = dateUtils.getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;

        schedule = new OneTimeSchedule(customer, wasteType, date);
        collection = new Collection(schedule);
    }

    @Test
    void testCollectionGettersAndSetters() {
        collection = new Collection(schedule);
        collection.setCollectionDate(date);
        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
        collection.setCancelLimitDays(5);
        collection.setWaste(Waste.WasteType.GLASS);

        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getCollectionDate());
        assertEquals(Waste.WasteType.GLASS, collection.getWaste());
        assertEquals(Collection.CollectionStatus.COMPLETED, collection.getCollectionStatus());
        assertEquals(5, collection.getCancelLimitDays());
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
        assertTrue(toStringOutput.contains(collection.getWaste().name()));
        assertTrue(toStringOutput.contains(collection.getCollectionStatus().name()));
        assertTrue(toStringOutput.contains(String.valueOf(Collection.CANCEL_LIMIT_DAYS)));
        assertTrue(toStringOutput.contains(String.valueOf(schedule.getScheduleId())));
        assertTrue(toStringOutput.contains(schedule.getScheduleCategory().name()));
    }

    @Test
    void testPersistence() {
        em.getTransaction().begin();
        customerDAO.insert(customer);
        oneTimeScheduleDAO.insert(schedule);
        collectionDAO.insert(collection);

        Collection found = em.find(Collection.class, collection.getCollectionId());
        assertNotNull(found);
        assertEquals(customer.getName(), found.getCustomer().getName());

        int foundId = found.getCollectionId();

        collectionDAO.delete(collection);

        Collection deleted = em.find(Collection.class, foundId);
        assertNull(deleted);
    }

    @Test
    void testCollectionValidation() {
        schedule = new OneTimeSchedule(null, null, null);
        Collection invalid = new Collection(null);
        invalid.setCollectionDate(date.minusDays(1));
        invalid.setCancelLimitDays(-1);
        invalid.setCollectionStatus(null);

        Set<ConstraintViolation<Collection>> violations = ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("The date must be today or in the future")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("The customer cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Cancellation days must be >= 0")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Schedule cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("The waste type cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("The collection status cannot be null")));
    }
}
