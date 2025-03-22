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

    // add analyzeType and getCategoryType method to analyze the waste
    public String getCategoryType() {
        if (category.equalsIgnoreCase("Plastic") || category.equalsIgnoreCase("Glass") || category.equalsIgnoreCase("Paper")) {
            return "Recyclable";
        }
        if (category.equalsIgnoreCase("Organic")) {
            return "Organic";
        }
        if (category.equalsIgnoreCase("Hazardous")) {
            return "Hazardous";
        }
        return "Unknown";
    }
    
    public String analyzeType() {
        String type = getCategoryType();
        switch (type) {
            case "Recyclable":
                return "Questo rifiuto può essere riciclato.";
            case "Organic":
                return "Rifiuto organico da smaltire separatamente.";
            case "Hazardous":
                return "Attenzione: rifiuto pericoloso da gestire con cautela.";
            default:
                return "Rifiuto non classificato.";
        }
    }
}
