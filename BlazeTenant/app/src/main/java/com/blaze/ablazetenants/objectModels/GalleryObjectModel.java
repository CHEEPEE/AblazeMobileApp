package com.blaze.ablazetenants.objectModels;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class GalleryObjectModel {
    private String businessId;
    private String imageUrl;
    private String key;


    public GalleryObjectModel(){

    }
    public GalleryObjectModel(String businessId, String imageUrl, String key
    ){
        this.businessId = businessId;
        this.imageUrl = imageUrl;
        this.key = key;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getKey() {
        return key;
    }
}
