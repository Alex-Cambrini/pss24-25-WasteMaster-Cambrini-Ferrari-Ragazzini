package it.unibo.wastemaster.viewmodels;

public class EmployeeRow {
	private final String name;
	private final String surname;
	private final String email;
	private final String role;
	private final String licence;
	private final String city;

	public EmployeeRow(String name, String surname, String email, String role, String licence, String city) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.role = role;
		this.licence = licence;
		this.city = city;
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
