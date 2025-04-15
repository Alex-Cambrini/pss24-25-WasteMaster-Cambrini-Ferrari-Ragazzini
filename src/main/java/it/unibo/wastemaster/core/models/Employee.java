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
        C1("Fino a 3.5 t"),
        C("Oltre 3.5 t"),
        C1E("3.5 t + rimorchio"),
        CE("3.5 t + rimorchio pesante");
    
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
        this.licenceType = (role == Role.OPERATOR) ? licenceType : null;
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
		if (this.role == Role.OPERATOR) {
			this.licenceType = licenceType;
		}
	}

    @Override
    public String getInfo() {
        return String.format("%s, EmployeeId: %d, Role: %s%s",
            super.getInfo(),
            employeeId,
            role,
            (role == Role.OPERATOR && licenceType != null) ? ", Licence: " + licenceType : ""
        );
    }
}
