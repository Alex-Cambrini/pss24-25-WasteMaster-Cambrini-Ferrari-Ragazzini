package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.models.Vehicle.LicenceType;
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
        return super.getInfo() + String.format(", EmployeeId: %d, Role: %s", employeeId, role, role == Role.OPERATOR ? ", Licence: " + licenceType : "");
    }
}
