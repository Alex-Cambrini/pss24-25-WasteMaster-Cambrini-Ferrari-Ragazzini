package it.unibo.wastemaster.core.models;

public class Client extends Person{
    private String ClientCode;

    // Constructor class Client
    public Client(int id, String name, String address, String email, String phone, String ClientCode) {
        super(id, name, address, email, phone);
        this.ClientCode = ClientCode;
    }

    // Getter method for obtaining the attribute of the class Client
    public String getClientCode() {
        return ClientCode;
    }

    // Setter method for setting the attribute of the class Client
    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
    }

    // Add getInfo method to return all the attributes of the class Client
    public String getInfo() {
        return super.getInfo() + String.format(", ClientCode: %s", ClientCode);
    }
}
