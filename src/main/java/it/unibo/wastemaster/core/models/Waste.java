package it.unibo.wastemaster.core.models;

public class Waste {
    private int wasteId;
    private WasteType type;
    private Boolean isRecyclable;
    private Boolean isDangerous;

    public enum WasteType {
        PLASTIC, 
        GLASS, 
        PAPER, 
        ORGANIC, 
        HAZARDOUS, 
        UNSORTED
    }

    public Waste(int wasteId, WasteType type, Boolean isRecyclable, Boolean isDangerous) {
        this.wasteId = wasteId;
        this.type = type;
        this.isRecyclable = isRecyclable;
        this.isDangerous = isDangerous;
    }

    public int getWasteId() {
        return wasteId;
    }

    public WasteType getType() {
        return type;
    }

    public void setType(WasteType type) {
        this.type = type;
    }

    public Boolean getIsRecyclable() {
        return isRecyclable;
    }

    public void setIsRecyclable(Boolean isRecyclable) {
        this.isRecyclable = isRecyclable;
    }

    public Boolean getIsDangerous() {
        return isDangerous;
    }

    public void setIsDangerous(Boolean isDangerous) {
        this.isDangerous = isDangerous;
    }
    
//TEST    
    // public static void main(String[] args) {

    //     Waste waste = new Waste(1, Waste.WasteType.PAPER, true, false);

    //     if (waste.getWasteId() == 1 && waste.getType() == Waste.WasteType.PAPER && 
    //         waste.getIsRecyclable() == true && waste.getIsDangerous() == false) {
    //         System.out.println("ok"); 
    //     } else {
    //         System.out.println("error");
    //     }

    //     waste.setType(Waste.WasteType.GLASS);
    //     waste.setIsRecyclable(false);
    //     waste.setIsDangerous(true);

    //     if (waste.getWasteId() == 1 && waste.getType() == Waste.WasteType.GLASS && 
    //         waste.getIsRecyclable() == false && waste.getIsDangerous() == true) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }
    // }


}
