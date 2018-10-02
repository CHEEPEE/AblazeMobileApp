package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class UserProfileObjectModel {
    private String userId;
    private String userImage;
    private String userName;
    private String contactNumber;
    private String email;
    private String userType;

    public UserProfileObjectModel(){

    }
    public UserProfileObjectModel(String userId,
                                  String userImage, String userName, String contactNumber, String email){
        this.userId=userId;
        this.userImage=userImage;
        this.userName= userName;
        this.contactNumber= contactNumber;
        this.email=email;
        this.userType = "owner";
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
}
