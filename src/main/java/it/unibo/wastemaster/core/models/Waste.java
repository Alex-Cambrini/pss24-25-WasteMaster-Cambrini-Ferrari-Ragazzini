package it.unibo.wastemaster.core.models;

public class Waste {
    private int id;
    private String type;
    private String category;

    // Constructor for Waste
    public Waste(int id, String type, String category) {
        this.id = id;
        this.type = type;
        this.category = category;
    }

    // add get for the attributes of the class Waste
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    // add set for the attributes of the class Waste
    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
