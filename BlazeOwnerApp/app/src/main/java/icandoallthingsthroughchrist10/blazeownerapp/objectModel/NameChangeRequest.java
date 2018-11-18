package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class NameChangeRequest {
    private String ownerAccountId;
    private boolean status;
    private @ServerTimestamp
    Date timeStamp;
    private String newName;



    public NameChangeRequest(){

    }
    public NameChangeRequest(String ownerAccountId,String newName
    ){
        this.newName = newName;
        this.ownerAccountId = ownerAccountId;
        this.status = false;

    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getOwnerAccountId() {
        return ownerAccountId;
    }

    public boolean isStatus() {
        return status;
    }

    public String getNewName() {
        return newName;
    }

}
