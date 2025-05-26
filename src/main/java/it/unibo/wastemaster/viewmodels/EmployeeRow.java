package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;

public class EmployeeRow {
	private final String name;
	private final String surname;
	private final String email;
	private final Role role;
	private final Licence licence;
	private final String city;

	public EmployeeRow(Employee employee) {
		this.name = employee.getName();
		this.surname = employee.getSurname();
		this.email = employee.getEmail();
		this.role = employee.getRole();
		this.licence = employee.getLicence();
		this.city = employee.getLocation().getCity();
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}

	public Licence getLicence() {
		return licence;
	}

	public String getCity() {
		return city;
	}
}
