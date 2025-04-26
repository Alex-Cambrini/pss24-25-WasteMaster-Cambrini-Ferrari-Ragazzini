package it.unibo.wastemaster.core.dao;

import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleDAOTest extends AbstractDatabaseTest {
    
    private Vehicle v1;
	private Vehicle v2;
	private Vehicle v3;

	@BeforeEach
	public void setUp() {
		super.setUp();

		v1 = new Vehicle("AA111AA", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
		v2 = new Vehicle("BB222BB", "Mercedes", "Sprinter", 2021, Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_MAINTENANCE);
		v3 = new Vehicle("CC333CC", "Fiat", "Ducato", 2022, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.OUT_OF_SERVICE);

		vehicleDAO.insert(v1);
		vehicleDAO.insert(v2);
		vehicleDAO.insert(v3);
	}

}
