package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class BoardingHouseProfileObjectModel {
    private String userId;
    private String name;
    private String address;
    private String type;
    private String owner;
    private String contactNumber;
    private String email;
    private String status;

    public BoardingHouseProfileObjectModel(){

    }
    public BoardingHouseProfileObjectModel(String userId,
                                           String name,
                                           String address,
                                           String type,
                                           String owner,
                                           String contactNumber,
                                           String email,
                                           String status
    ){
       this.userId = userId;
       this.name = name;
       this.address = address;
       this.type = type;
       this.owner = owner;
       this.contactNumber = contactNumber;
       this.email = email;
       this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

}
