package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class BhouseLocationModel {
    private String key;
    private String locationName;
    private double locationLatitude;
    private double locationLongitude;


    public BhouseLocationModel(){

    }
    public BhouseLocationModel(String key, String locationName, double locationLatitude, double locationLongitude
    ){
       this.key = key;
       this.locationName = locationName;
       this.locationLatitude = locationLatitude;
       this.locationLongitude = locationLongitude;
    }

    public String getKey() {
        return key;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public String getLocationName() {
        return locationName;
    }
}
