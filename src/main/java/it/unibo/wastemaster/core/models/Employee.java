package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee")
public class Employee extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeId;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
	private LicenceType licenceType;

    public enum LicenceType {
        B("Fino a 3.5 t"),
        C1("3.5 t - 7.5 t"),
        C("Oltre 7.5 t");
    
        private final String description;
    
        LicenceType(String description) {
            this.description = description;
        }
    
        public String getDescription() {
            return description;
        }
    }
    
    
    public enum Role {
        ADMINISTRATOR,
        OFFICE_WORKER,
        OPERATOR
    }

    public Employee(String name, String surname, Location address, String email, String phone, Role role, LicenceType licenceType) {
        super(name, surname, address, email, phone);
        this.role = role;

        if (licenceType == null) {
            throw new IllegalArgumentException("All employees must have a licence");
        }
    
        if (role == Role.OPERATOR && licenceType == LicenceType.B) {
            throw new IllegalArgumentException("Operators must have at least a C1 licence");
        }

        this.licenceType = licenceType;
    }

    public Employee() {
        super();
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LicenceType getLicenceType() {
		return licenceType;
	}

    public void setLicenceType(LicenceType licenceType) {
		if (licenceType == null) {
            throw new IllegalArgumentException("LicenceType cannot be null");
        }
    
        if (this.role == Role.OPERATOR && licenceType == LicenceType.B) {
            throw new IllegalArgumentException("Operators must have at least a C1 licence");
        }
    
		this.licenceType = licenceType;
	}

    @Override
    public String getInfo() {
        return String.format("%s, EmployeeId: %d, Role: %s, Licence: %s",
            super.getInfo(),
            employeeId,
            role,
            licenceType != null ? licenceType : "N/A"
        );
    }
}
