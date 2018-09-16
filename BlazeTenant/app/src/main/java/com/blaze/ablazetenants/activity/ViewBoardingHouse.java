package com.blaze.ablazetenants.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.appModule.GlideApp;
import com.blaze.ablazetenants.objectModels.BoardingHouseProfileObjectModel;
import com.blaze.ablazetenants.objectModels.GalleryObjectModel;
import com.blaze.ablazetenants.objectModels.GeneralInformationObjectModel;
import com.blaze.ablazetenants.views.GalleryRecyclerViewAdapter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ViewBoardingHouse extends AppCompatActivity {
    String key;
    TextView bhouseName,priceVal,spaceVal,capacityVal,waterBill,electBill,onwerNumber;
    ImageView app_bar_image;
    Context context;
    RecyclerView imageGalList;
    GalleryRecyclerViewAdapter galleryRecyclerViewAdapter;
    ArrayList<GalleryObjectModel> galleryObjectModelArrayList = new ArrayList<>();

    FirebaseFirestore ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_boarding_house);
        key = getIntent().getExtras().getString("key");
        context = ViewBoardingHouse.this;
        bhouseName = (TextView) findViewById(R.id.bhouseName);
        priceVal = (TextView) findViewById(R.id.priceVal);
        spaceVal = (TextView) findViewById(R.id.spaceVal);
        ref = FirebaseFirestore.getInstance();
        capacityVal = (TextView) findViewById(R.id.capacityVal);
        app_bar_image = (ImageView) findViewById(R.id.app_bar_image);
        waterBill = (TextView) findViewById(R.id.waterbill);
        electBill = (TextView) findViewById(R.id.electBill);
        onwerNumber = (TextView) findViewById(R.id.onwerNumber);
        imageGalList = (RecyclerView) findViewById(R.id.galList);
        ref.collection("houseProfiles").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                BoardingHouseProfileObjectModel boardingHouseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
                bhouseName.setText(boardingHouseProfileObjectModel.getName());
                onwerNumber.setText(boardingHouseProfileObjectModel.getContactNumber());
            }
        });


        FirebaseFirestore.getInstance()
                .collection("generalInformation")
                .document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                GeneralInformationObjectModel generalInformationObjectModel = documentSnapshot.toObject(GeneralInformationObjectModel.class);
                if (e != null) {
                    Log.w("generalInformation", "Listen failed.", e);
                    return;
                }else {
                    try {
                        priceVal.setText("Php "+generalInformationObjectModel.getPrice()+"");
                        spaceVal.setText("Space Available: "+generalInformationObjectModel.getAvailable()+"");
                        capacityVal.setText(generalInformationObjectModel.getRoomCapacity()+" beds per room");
                        if (generalInformationObjectModel.isCurrentBill()){
                            electBill.setVisibility(View.VISIBLE);
                        }else {
                            electBill.setVisibility(View.INVISIBLE);
                        }
                        if (generalInformationObjectModel.isWaterBil()){
                            waterBill.setVisibility(View.VISIBLE);
                        }else {
                            waterBill.setVisibility(View.INVISIBLE);
                        }

                    }catch (NullPointerException ex){
                        priceVal.setText("N/A");
                        spaceVal.setText("N/A");
                        capacityVal.setText("N/A");
                    }
                }
            }
        });

        FirebaseFirestore.getInstance()
                .collection("imageBanner")
                .document(key)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        try {
                            GlideApp.with(context)
                                    .load(documentSnapshot.getData().get("imageBanner"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                                    .into(app_bar_image);
                        }catch (NullPointerException ex){

                        }
                    }
                });
        getGallery();

    }
    void  getGallery(){
        galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(context,galleryObjectModelArrayList,key);
        imageGalList.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        imageGalList.setAdapter(galleryRecyclerViewAdapter);
        ref.collection("imageGallery")
                .whereEqualTo("businessId",key)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                            return;
                        }
                        galleryObjectModelArrayList.clear();
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            GalleryObjectModel galleryObjectModel = dc.toObject(GalleryObjectModel.class);
                            galleryObjectModelArrayList.add(galleryObjectModel);
                        }
                        galleryRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(imageGalList);
    }
}