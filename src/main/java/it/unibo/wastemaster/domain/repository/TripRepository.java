package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository {

    void save(Trip trip);

    void update(Trip trip);

    Optional<Trip> findById(int tripId);

    void delete(Trip trip);

    List<Trip> findAll();

    List<Trip> findByOperator(Employee operator);

    List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end);

    List<Employee> findQualifiedDrivers(LocalDateTime start, LocalDateTime end,
                                        List<Licence> allowedLicences);

    List<Employee> findAvailableOperatorsExcludeDriver(LocalDateTime start,
                                                       LocalDateTime end,
                                                       Employee driver);

    List<Employee> findQualifiedDriversToEdit(LocalDateTime start, LocalDateTime end,
                                              List<Licence> allowedLicences,
                                              Trip tripToEdit);

    List<Employee> findAvailableOperatorsExcludeDriverToEdit(LocalDateTime start,
                                                             LocalDateTime end,
                                                             Employee driver,
                                                             Trip tripToEdit);

    List<String> findAvailablePostalCodes(LocalDate date);

    List<Trip> findLast5Inserted();

    int countCompleted();
}
