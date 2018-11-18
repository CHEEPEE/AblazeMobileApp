package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class GeneralInformationObjectModel {
    private String userId;
    private int price;
    private int available;
    private int roomCapacity;
    private int roomRevCapacity;
    private int roomPrice;
    private int roomAvailable;
    private boolean waterBil;
    private boolean currentBill;
    private String description;
    private String roomType;

    public GeneralInformationObjectModel(){

    }
    public GeneralInformationObjectModel(String userId,
                                         int price,
                                         int available,
                                         int roomCapacity,
                                         boolean waterBil,
                                         boolean currentBill,
                                         String description,
                                         String roomType,
                                         int roomRevCapacity,
                                         int roomAvailable,
                                         int roomPrice
    ){
        this.userId = userId;
        this.price = price;
        this.available = available;
        this.roomCapacity = roomCapacity;
        this.waterBil = waterBil;
        this.currentBill = currentBill;
        this.description = description;
        this.roomType = roomType;
        this.roomRevCapacity = roomRevCapacity;
        this.roomAvailable = roomAvailable;
        this.roomPrice = roomPrice;
    }

    public String getUserId() {
        return userId;
    }

    public int getAvailable() {
        return available;
    }

    public int getPrice() {
        return price;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public boolean isCurrentBill() {
        return currentBill;
    }

    public boolean isWaterBil() {
        return waterBil;
    }

    public String getDescription() {
        return description;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public int getRoomAvailable() {
        return roomAvailable;
    }

    public int getRoomRevCapacity() {
        return roomRevCapacity;
    }

}
