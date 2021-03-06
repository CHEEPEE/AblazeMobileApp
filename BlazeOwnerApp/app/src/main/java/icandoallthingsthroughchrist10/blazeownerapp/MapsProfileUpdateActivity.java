package icandoallthingsthroughchrist10.blazeownerapp;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import icandoallthingsthroughchrist10.blazeownerapp.objectModel.BhouseLocationModel;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MapsProfileUpdateActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = MapsProfileUpdateActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 14;
    private boolean mLocationPermissionGranted = false;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private UiSettings mUiSettings;
    private LatLng currnetLoc;
    TextView lblLocation;
    private Marker marker;
    private double restoLocationLatitude;
    private double restoLocationLongitude;
    private ImageView confirmLocation;
    private FirebaseFirestore mDataBase;
    private FirebaseAuth mAuth;
    private LatLng restoLocation;
    String businessLocation = "businessLocations";
    BhouseLocationModel bhouseLocationModel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_profile_update);
//        businessKey = getIntent().getExtras().getString("key");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mDataBase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mapFragment.getMapAsync(this);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        lblLocation = (TextView) findViewById(R.id.lblLocation);
        confirmLocation = (ImageView) findViewById(R.id.confirmLocation);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        mDataBase.collection("bhouseLocation").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if (documentSnapshot.getData() != null){
                   bhouseLocationModel = documentSnapshot.toObject(BhouseLocationModel.class);
                   restoLocationLatitude = bhouseLocationModel.getLocationLatitude();
                   restoLocationLongitude = bhouseLocationModel.getLocationLongitude();

                   restoLocation = new LatLng(restoLocationLatitude,restoLocationLongitude);
                   getRestaurantCurrentLocation();
                   setLocation();
               }else {
                   restoLocationLatitude = 10.769402621680829;
                   restoLocationLongitude = 122.05785971134901;

                   restoLocation = new LatLng(restoLocationLatitude,restoLocationLongitude);
                   getRestaurantCurrentLocation();
                   setLocation();
               }

            }
        });


        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressLocation = lblLocation.getText().toString();
                double latitude = restoLocationLatitude;
                double longitude = restoLocationLongitude;

                BhouseLocationModel bhouseLocationModel = new BhouseLocationModel(mAuth.getUid(),addressLocation,latitude,longitude);
                mDataBase.collection("bhouseLocation").document(mAuth.getUid()).set(bhouseLocationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
            }
        });
        Toast.makeText(MapsProfileUpdateActivity.this,
                "This is your current location. Long press the red marker and drag to relocate.", Toast.LENGTH_LONG).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


        updateLocationUI();

        if (restoLocation!=null){
            getRestaurantCurrentLocation();
        }else {

            mDataBase.collection("bhouseLocation").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.getData() != null){
                        BhouseLocationModel bhouseLocationModel = documentSnapshot.toObject(BhouseLocationModel.class);
                        restoLocationLatitude = bhouseLocationModel.getLocationLatitude();
                        restoLocationLongitude = bhouseLocationModel.getLocationLongitude();
                    }else {
                        restoLocationLatitude = 10.769402621680829;
                        restoLocationLongitude = 122.05785971134901;
                    }
                }
            });
//            mDataBase.child(businessLocation).child(businessKey).child("locationLatitude").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue(Double.class)!=null){
//                        restoLocationLatitude = dataSnapshot.getValue(Double.class);
//                        mDataBase.child(businessLocation).child(businessKey).child("locationLongitude").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                restoLocationLongitude = dataSnapshot.getValue(Double.class);
//                                getRestaurantCurrentLocation();
//                            }
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                    else {
//                        getDeviceLocation();
//                    }
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        }


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                currnetLoc = new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                try {
                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(arg0.getPosition().latitude, arg0.getPosition().longitude, 1);
                    if (addresses.isEmpty()) {
                        Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                           for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                               Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                restoLocationLatitude = arg0.getPosition().latitude;
                                restoLocationLongitude = arg0.getPosition().longitude;
                           }
                        }
                    }
                }catch (IOException e){
                    Log.i("System out error", e.toString());
//                    Utils.callToast(MapsProfileUpdateActivity.this,"Please Check Internet Connection");
                }
            }
            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub


            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    marker.remove();
                }catch (NullPointerException e){

                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                marker.setDraggable(true);
                try {
                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude,latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        Log.i("System out", "onMarkerDragEnd... waiting for location name"+latLng.latitude+"..."+latLng.longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                            for (int i = 0;i<addresses.size();i++){
                                Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                restoLocationLatitude = latLng.latitude;
                                restoLocationLongitude = latLng.longitude;

                            }
                        }
                    }
                }catch (IOException e){
                    Log.i("System out error", e.toString());
//                    Utils.callToast(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */

    if (mLocationPermissionGranted){
        SmartLocation.with(MapsProfileUpdateActivity.this).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),
                                location.getLongitude()), DEFAULT_ZOOM));

                marker= mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Your Current Location"));
                marker.setDraggable(true);

                try {

                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.isEmpty()) {

                        //   Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                            for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                                Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                restoLocationLatitude = location.getLatitude();
                                restoLocationLongitude = location.getLongitude();
                            }
                        }
                    }
                }catch (IOException e){
                    Log.i("System out error", e.toString());
//                    Utils.callToast(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                }

            }
        });
    }

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setLocation(){
        try {
            if (mLocationPermissionGranted) {
                LatLng latLng = null;
                    latLng =  new LatLng(restoLocationLatitude,restoLocationLongitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latLng.latitude,latLng.longitude), DEFAULT_ZOOM));

                marker= mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                marker.setDraggable(true);

                currnetLoc = latLng;
                try {
                    lblLocation.setText(bhouseLocationModel.getLocationName());
                }catch (NullPointerException e){

                }
                try {

                    Geocoder geo = new Geocoder(MapsProfileUpdateActivity.this, Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latLng.latitude,latLng.longitude, 1);
                    if (addresses.isEmpty()) {
                        //   Log.i("System out", "onMarkerDragEnd... waiting for location name"+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }
                    else {
                        if (addresses.size() > 0) {
                            for (int i = 0;i<addresses.size();i++){
                             /*  Log.i("Location",addresses.get(i).getAddressLine(0)+", "+addresses.get(i).getFeatureName() + ", " + addresses.get(i).getLocality() +", " + addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getFeatureName() + ", " + addresses.get(i).getCountryName());*/
                                Log.i("Location",addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());
                                lblLocation.setText(addresses.get(i).getLocality()+", "+addresses.get(i).getSubAdminArea()+", "+addresses.get(i).getAdminArea()+ ", " + addresses.get(i).getCountryName());

                            }
                        }
                    }
                }catch (IOException e){
//                    Utils.callToast(MapsProfileUpdateActivity.this,"Please Check Internet Connection");

                    Log.i("System out error", e.toString());
                }
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getRestaurantCurrentLocation(){
        mMap.addMarker(new MarkerOptions().position(restoLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restoLocation, DEFAULT_ZOOM));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
