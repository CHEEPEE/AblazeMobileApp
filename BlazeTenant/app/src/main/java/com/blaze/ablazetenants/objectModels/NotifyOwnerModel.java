package com.blaze.ablazetenants.objectModels;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class NotifyOwnerModel {
   private String message;
   private String number;


    public NotifyOwnerModel(){

    }
    public NotifyOwnerModel(String message,String number
    ){
       this.message = message;
       this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
