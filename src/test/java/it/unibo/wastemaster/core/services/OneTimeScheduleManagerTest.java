package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OneTimeScheduleManagerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Location location;
    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();

        location = new Location("Via Milano", "22", "Modena", "41100");
        customer = new Customer("Luca", "Verdi", location, "luca.verdi@example.com",
                "3334567890");
        waste = new Waste("plastic", true, false);

        getLocationDAO().insert(location);
        getCustomerDAO().insert(customer);
        getWasteDAO().insert(waste);
    }

    @Test
    void testCreateOneTimeSchedule() {
        LocalDate invalidDate = LocalDate.now().plusDays(1);
        LocalDate validDate = LocalDate.now().plusDays(3);

        assertThrows(IllegalArgumentException.class, () -> {
            getOneTimeScheduleManager().createOneTimeSchedule(customer, waste,
                    invalidDate);
        });

        OneTimeSchedule newSchedule = getOneTimeScheduleManager()
                .createOneTimeSchedule(customer, waste, validDate);
        assertNotNull(newSchedule);
        assertEquals(ScheduleStatus.ACTIVE, newSchedule.getScheduleStatus());

        Collection associatedCollection =
                getCollectionManager().getAllCollectionBySchedule(newSchedule).get(0);
        assertNotNull(associatedCollection);
        assertEquals(Collection.CollectionStatus.PENDING,
                associatedCollection.getCollectionStatus());
    }

    @Test
    void testSoftDeleteOneTimeSchedule() {
        LocalDate validDate = LocalDate.now().plusDays(3);

        // 1) Null args
        assertThrows(IllegalArgumentException.class,
                () -> getOneTimeScheduleManager().softDeleteOneTimeSchedule(null));

        // 2) Already CANCELLED → false
        OneTimeSchedule cancelledSchedule =
                new OneTimeSchedule(customer, waste, validDate);
        cancelledSchedule.setScheduleStatus(ScheduleStatus.CANCELLED);
        assertFalse(
                getOneTimeScheduleManager().softDeleteOneTimeSchedule(cancelledSchedule));

        // 3) ACTIVE → CANCELLED
        OneTimeSchedule activeSchedule = getOneTimeScheduleManager()
                .createOneTimeSchedule(customer, waste, validDate);
        boolean deleted =
                getOneTimeScheduleManager().softDeleteOneTimeSchedule(activeSchedule);
        assertTrue(deleted);

        // Check collection also cancelled
        Collection cancelledCollection =
                getCollectionManager().getAllCollectionBySchedule(activeSchedule).get(0);
        assertNotNull(cancelledCollection);
        assertEquals(Collection.CollectionStatus.CANCELLED,
                cancelledCollection.getCollectionStatus());

        // 4) Try to delete again → false
        assertFalse(
                getOneTimeScheduleManager().softDeleteOneTimeSchedule(activeSchedule));
    }
}
