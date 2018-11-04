package com.blaze.ablazetenants.objectModels;

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


    public AccountThatNotifiedModel(){

    }
    public AccountThatNotifiedModel(String tenantAccountId,String ownerAccountId,boolean status
    ){
        this.tenantAccountId = tenantAccountId;
        this.ownerAccountId = ownerAccountId;
        this.status = status;
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
}
