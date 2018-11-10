package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class AccountThatNotifiedModel {
    private String tenantAccountId;
    private String ownerAccountId;
    private boolean status;
    private @ServerTimestamp
    Date timeStamp;
    private String key;


    public AccountThatNotifiedModel(){

    }
    public AccountThatNotifiedModel(String key, String tenantAccountId, String ownerAccountId, boolean status
    ){
        this.tenantAccountId = tenantAccountId;
        this.ownerAccountId = ownerAccountId;
        this.status = status;
        this.key = key;
    }

    public String getOwnerAccountId() {
        return ownerAccountId;
    }

    public String getTenantAccountId() {
        return tenantAccountId;
    }

    public boolean isStatus() {
        return status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getKey() {
        return key;
    }
}
