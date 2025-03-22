package it.unibo.wastemaster.core.models;

public class Person {
    protected int id;
    protected String name;
    protected String surname;
    protected Location address;
    protected String email;
    protected String phone;


    // Constructor class Person
    public Person(int id, String name, String surname, Location address, String email, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
    public Location getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAddress(Location address) {
        this.address = address;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInfo() {
        return String.format("ID: %d, Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s", id, name, surname, address, email, phone);
    }

    // //TEST
    // public static void main(String[] args) {

    //     Location location = new Location(0, "Via Roma", "10", "Milano", "Italy"); 

    //     Person person = new Person(1, "Mario", location, "mario@example.com", "1234567890") {
           
    //     };
   
    //     System.out.println("Info iniziali: " + person.getInfo());
    //     System.out.println("ID: " + person.getId());
    //     System.out.println("Nome: " + person.getName());
    //     System.out.println("Email: " + person.getEmail());
    //     System.out.println("Telefono: " + person.getPhone());
        

    //     Location personAddress = person.getAddress();
    //     System.out.println("Indirizzo: " + personAddress.getStreet() + " " + personAddress.getCivicNumber() + ", " + personAddress.getCity() + ", " + personAddress.getCity());
    
    //     // Modifica
    //     person.setName("Giovanni");
    //     person.setPhone("0987654321");
    //     Location newLocation = new Location(1,"Via Milano", "5", "Torino", "Italy");
    //     person.setAddress(newLocation);
    
    //     // Verifica
    //     System.out.println("Info dopo la modifica: " + person.getInfo());
    // }
    
}
