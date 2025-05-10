package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employee")
public class Employee extends Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    @NotNull (message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Licence type cannot be null")
	private LicenceType licenceType;

    public enum LicenceType {
        NONE("None"),
        B("Fino a 3.5 t"),
        C1("3.5 t - 7.5 t"),
        C("Oltre 7.5 t");

    
        private final String licenceDescription;
    
        LicenceType(String licenceDescription) {
            this.licenceDescription = licenceDescription;
        }
    
        public String getLicenceDescription() {
            return licenceDescription;
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
        this.licenceType = licenceType;
    }

    public Employee() {
        super();
    }

    public Integer getEmployeeId() {
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
		this.licenceType = licenceType;
	}

    @Override
    public String toString() {
        return String.format(
                "Employee {ID: %d, Name: %s %s, Email: %s, Phone: %s, Location: %s, Role: %s, Licence: %s}",
                employeeId,
                getName(),
                getSurname(),
                getEmail(),
                getPhone(),
                getLocation() != null ? getLocation().toString() : "N/A",
                role,
                licenceType);
    }
}
