package it.unibo.wastemaster.core.services;

import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleManagerTest extends AbstractDatabaseTest {

	private VehicleManager vehicleManager;
	private Vehicle v1;
	private Vehicle v2;
	private Vehicle v3;

	@BeforeEach
	public void setUp() {
		super.setUp();
		vehicleManager = new VehicleManager(vehicleDAO);
        em.getTransaction().begin();

		v1 = new Vehicle("DD444DD", "Renault", "Master", 2023, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
		v2 = new Vehicle("EE555EE", "MAN", "TGE", 2022, Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_MAINTENANCE);
		v3 = new Vehicle("FF666FF", "Ford", "Transit", 2021, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.OUT_OF_SERVICE);

		vehicleDAO.insert(v1);
		vehicleDAO.insert(v2);
		vehicleDAO.insert(v3);
	}

}
