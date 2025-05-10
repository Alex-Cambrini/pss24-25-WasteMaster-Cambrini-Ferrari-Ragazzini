package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class TripTest extends AbstractDatabaseTest {

    private Trip trip;
    private Vehicle vehicle;
    private Employee operator;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    
}