package it.unibo.wastemaster.core.models;

public class Person {
    protected int id;
    protected String name;
    protected String surname;
    protected Location address;
    protected String email;
    protected String phone;


    // Constructor class Person
    public Person(int id, String name, Location address, String email, String phone) {
        this.id = id;
        this.name = name;
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
        return String.format("ID: %d, Name: %s, Address: %s, Email: %s, Phone: %s", id, name, address, email, phone);
    }


    // public static void main(String[] args) {
    //     // Creazione di un oggetto Location (modifica il costruttore di Location come necessario)
    //     Location location = new Location(0, "Via Roma", "10", "Milano", "Italy");  // Esempio di Location
        
    //     // Creazione di un oggetto Person (usiamo una classe concreta per testare, poiché Person è astratta)
    //     Person person = new Person(1, "Mario", location, "mario@example.com", "1234567890") {
    //         // Classe anonima per implementare Person (essendo astratta)
    //     };
        
    //     // Test del metodo getInfo
    //     System.out.println("Info iniziali: " + person.getInfo());
    
    //     // Test dei metodi get per tutti i campi
    //     System.out.println("ID: " + person.getId());
    //     System.out.println("Nome: " + person.getName());
    //     System.out.println("Email: " + person.getEmail());
    //     System.out.println("Telefono: " + person.getPhone());
        
    //     // Test dei metodi get per l'indirizzo (Location)
    //     Location personAddress = person.getAddress();
    //     System.out.println("Indirizzo: " + personAddress.getStreet() + " " + personAddress.getCivicNumber() + ", " + personAddress.getCity() + ", " + personAddress.getCity());
    
    //     // Modifica dei campi con i metodi set
    //     person.setName("Giovanni");
    //     person.setPhone("0987654321");
    //     Location newLocation = new Location(1,"Via Milano", "5", "Torino", "Italy");
    //     person.setAddress(newLocation);
    
    //     // Verifica della modifica con getInfo
    //     System.out.println("Info dopo la modifica: " + person.getInfo());
    // }
    
}
