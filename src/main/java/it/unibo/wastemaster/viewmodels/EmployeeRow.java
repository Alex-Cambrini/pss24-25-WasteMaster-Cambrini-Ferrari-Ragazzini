package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Employee;

public class EmployeeRow {
	private final String name;
	private final String surname;
	private final String email;
	private final String role;
	private final String licence;
	private final String city;

	public EmployeeRow(Employee employee) {
		this.name = employee.getName();
		this.surname = employee.getSurname();
		this.email = employee.getEmail();
		this.role = employee.getRole().toString();
		this.licence = employee.getLicence().toString();
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

	public String getRole() {
		return role;
	}

	public String getLicence() {
		return licence;
	}

	public String getCity() {
		return city;
	}
}
