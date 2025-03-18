package it.unibo.wastemaster.core.models;

public abstract class Person {
    protected int id;
    protected String name;
    protected String address;
    protected String email;
    protected String phone;


    // Constructor class Person
    public Person(int id, String name, String address, String email, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
    
}
