package it.unibo.wastemaster.core.services;



import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;




public class TripManagerTest {

    private TripManager service;

    @BeforeEach
    public void setUp() {
        service = new  TripManager();
    }


    @Test
    public void testAddAndGetRoute() {
        Vehicle truck = new Vehicle();
        truck.setPlate("TRUCK-001");

         Schedule schedule = new OneTimeSchedule(
                new Customer("Customer1", "Address1", null, null, null),
                Waste.WasteType.UNSORTED,
                LocalDate.of(2023, 10, 15)
        );
       

        List<Location> stops = List.of(new Location(), new Location());
        Trip route = new Trip(1, stops, truck, schedule);

        service.planRoute(route);

        Trip retrieved = service.getRoute(1);
        assertNotNull(retrieved);
        assertEquals("TRUCK-001", retrieved.getTruck().getPlate());
    }


    @Test
    public void testAddAndRetrieveSchedules() {
        OneTimeSchedule oneTime = new OneTimeSchedule(
            new Customer("Customer1", "Address1", null, null, null),
            Waste.WasteType.UNSORTED,
            LocalDate.of(2023, 10, 15)
    );


        RecurringSchedule recurring = new RecurringSchedule(
            new Customer("Customer2", "Address2", null, null, null),
            Waste.WasteType.PAPER,
            LocalDate.of(2023, 10, 1),
            RecurringSchedule.Frequency.WEEKLY
    );
         
        

        service.addOneTimeSchedule(oneTime);
        service.addRecurringSchedule(recurring);

        assertEquals(1, service.getOneTimeSchedules().size());
        assertEquals(1, service.getRecurringSchedules().size());
    }


    @Test
    public void testUpdateAndRemoveRoute() {
        Trip route = new Trip();
        route.setId(10);
        service.planRoute(route);

        assertNotNull(service.getRoute(10));
        service.removeRoute(10);
        assertNull(service.getRoute(10));
    }








    
}
