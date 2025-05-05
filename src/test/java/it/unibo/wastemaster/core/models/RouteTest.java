package it.unibo.wastemaster.core.models;


import it.unibo.wastemaster.core.models.Route;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RouteTest {

    @Test
    public void testRouteGettersAndSetters() {
       Vehicle truck = new Vehicle("TRUCK-001", "Brand", "Model", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
        

        Schedule schedule = new OneTimeSchedule(
                new Customer("Customer1", "Address1", null, null, null),
                Waste.WasteType.UNSORTED,
                LocalDate.of(2023, 10, 15)
        );
       

        Location stop1 = new Location();
        Location stop2 = new Location();
        List<Location> stops = List.of(stop1, stop2);
        
        Route route = new Route(100, stops, truck, schedule);

        assertEquals(100, route.getId());
        assertEquals(stops, route.getStops());
        assertEquals(truck, route.getTruck());
        assertEquals(schedule, route.getSchedule());

    }

   

    
        
   
 








}
