package com.blaze.ablazetenants.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.appModule.GlideApp;
import com.blaze.ablazetenants.objectModels.BoardingHouseProfileObjectModel;
import com.blaze.ablazetenants.objectModels.GalleryObjectModel;
import com.blaze.ablazetenants.objectModels.GeneralInformationObjectModel;
import com.blaze.ablazetenants.views.GalleryRecyclerViewAdapter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ViewBoardingHouse extends AppCompatActivity {
    String key;
    TextView bhouseName,priceVal,spaceVal,capacityVal,waterBill,electBill,onwerNumber,bookReservation,description;
    ImageView app_bar_image;
    Context context;
    RecyclerView imageGalList;
    GalleryRecyclerViewAdapter galleryRecyclerViewAdapter;
    ArrayList<GalleryObjectModel> galleryObjectModelArrayList = new ArrayList<>();
    String numberToCall;
    BoardingHouseProfileObjectModel boardingHouseProfileObjectModel;

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
        bookReservation = (TextView) findViewById(R.id.bookReservation);
        description = (TextView) findViewById(R.id.description);
        ref.collection("houseProfiles").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                boardingHouseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
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
                        description.setText(generalInformationObjectModel.getDescription());
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

        findViewById(R.id.imgCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourfunction();
            }
        });
        bookReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookReservation();
            }
        });

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


    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;
    public void yourfunction() {
        if (ContextCompat.checkSelfPermission(ViewBoardingHouse.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ViewBoardingHouse.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            executeCall();
        }
    }

    private void executeCall() {
        // start your call here
        ViewBoardingHouse.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+boardingHouseProfileObjectModel.getContactNumber()));
                startActivity(callIntent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                    executeCall();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    void bookReservation(){
        final Dialog dialog = new Dialog(ViewBoardingHouse.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_book_reservation_menu);
        Window window = dialog.getWindow();
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView messenger = (TextView) dialog.findViewById(R.id.messenger);
        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boardingHouseProfileObjectModel.getMessengerId()!=null){
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(boardingHouseProfileObjectModel.getMessengerId()));
                    startActivity(i);
                }else {
                    Toast.makeText(context,"Messenger not Available",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourfunction();
            }
        });
        dialog.findViewById(R.id.mesage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSms();
            }
        });
        dialog.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClibBoard();
            }
        });


        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    void openSms(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+boardingHouseProfileObjectModel.getContactNumber()));
        sendIntent.putExtra("sms_body","");
        startActivity(sendIntent);
    }

    void copyToClibBoard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("userId", boardingHouseProfileObjectModel.getContactNumber());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(ViewBoardingHouse.this,"Copy to Clipboard "+boardingHouseProfileObjectModel.getContactNumber(),Toast.LENGTH_SHORT).show();
    }



}
