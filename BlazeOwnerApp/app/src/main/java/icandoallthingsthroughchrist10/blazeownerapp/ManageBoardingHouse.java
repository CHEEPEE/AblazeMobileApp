package icandoallthingsthroughchrist10.blazeownerapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import icandoallthingsthroughchrist10.blazeownerapp.objectModel.BoardingHouseProfileObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GalleryObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GeneralInformationObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.views.GalleryRecyclerViewAdapter;

public class ManageBoardingHouse extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    boolean access_storage;
    private static final int storagepermision_access_code = 548;
    boolean imageSet = false;
    Uri bannerUri;
    ImageView roomPhoto,editContact;
    String businessKey;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TextView name,owner,address,mail,number,status,setting,priceVal, spaceVal,capacityVal,manageGallery,des;
    Context context;
    RecyclerView imageGalList;
    GalleryRecyclerViewAdapter galleryRecyclerViewAdapter;
    ArrayList<GalleryObjectModel> galleryObjectModelArrayList = new ArrayList<>();
    BoardingHouseProfileObjectModel boardingHouseProfileObjectModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_boarding_house);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        context = ManageBoardingHouse.this;
        name = (TextView) findViewById(R.id.name);
        owner = (TextView) findViewById(R.id.owner);
        address = (TextView) findViewById(R.id.address);
        mail = (TextView) findViewById(R.id.mail);
        number = (TextView) findViewById(R.id.number);
        status = (TextView) findViewById(R.id.status);
        setting = (TextView) findViewById(R.id.settings);
        priceVal = (TextView) findViewById(R.id.priceVal);
        spaceVal = (TextView) findViewById(R.id.spaceVal);
        capacityVal = (TextView) findViewById(R.id.capacityVal);
        manageGallery = (TextView) findViewById(R.id.manageGallery);
        imageGalList = (RecyclerView) findViewById(R.id.imageGalList);
        editContact = (ImageView) findViewById(R.id.editContact);
        des = (TextView) findViewById(R.id.desciption);

        db.collection("houseProfiles").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               try {
                   BoardingHouseProfileObjectModel object = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
                   boardingHouseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
                   name.setText(object.getName());
                   owner.setText(object.getOwner());
                   address.setText(object.getAddress());
                   mail.setText(object.getEmail());
                   number.setText(object.getContactNumber());
                   status.setText((object.getStatus().equals("pending"))? "Pending Approval": object.getStatus());
                   businessKey = object.getUserId();
                   getGallery();
               }catch (NullPointerException ex){

               }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog();
            }
        });

        findViewById(R.id.update_gen_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGenInfo();
            }
        });
        findViewById(R.id.uploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performFileSearch();
            }
        });
        manageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ManageGallery.class);
                i.putExtra("key",businessKey);
                startActivity(i);
            }
        });

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });





        setAppBarImage();
        getGeneralInfo();


    }

    void  getGallery(){
        galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(context,galleryObjectModelArrayList,businessKey);
        imageGalList.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        imageGalList.setAdapter(galleryRecyclerViewAdapter);
        db.collection("imageGallery")
                .whereEqualTo("businessId",businessKey)
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
        try {
            snapHelper.attachToRecyclerView(imageGalList);
        }catch (IllegalStateException e){

        }
    }
    void getGeneralInfo(){
        FirebaseFirestore.getInstance().collection("generalInformation").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("generalInformation", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("generalInformation", "Current data: " + documentSnapshot.getData());
                    GeneralInformationObjectModel generalInformationObjectModel = documentSnapshot.toObject(GeneralInformationObjectModel.class);
                    try {
                        priceVal.setText(generalInformationObjectModel.getPrice()+"");
                        spaceVal.setText(generalInformationObjectModel.getAvailable()+"");
                        capacityVal.setText(generalInformationObjectModel.getRoomCapacity()+"");
                        des.setText(generalInformationObjectModel.getDescription());
                    }catch (NullPointerException ex){

                    }
//                    waterBill.setChecked(generalInformationObjectModel.isWaterBil());
//                    curretBill.setChecked(generalInformationObjectModel.isCurrentBill());

                } else {
                    Log.d("generalInformation", "Current data: null");
                }

            }
        });
    }

    private void signOut(){
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(),LoginActivity.class/*To ur activity calss*/);
                        Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }

    void updateContact(){
        final Dialog dialog = new Dialog(ManageBoardingHouse.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_update_contact);
        Window window = dialog.getWindow();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final EditText contactNumber = (EditText) dialog.findViewById(R.id.contactNumber);
        contactNumber.setText(boardingHouseProfileObjectModel.getContactNumber());

        dialog.findViewById(R.id.saveChanges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("houseProfiles")
                        .document(auth.getUid()).update("contactNumber",contactNumber.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    void settingDialog(){
        final Dialog dialog = new Dialog(ManageBoardingHouse.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_settings);
        Window window = dialog.getWindow();
        dialog.findViewById(R.id.tickets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ManageReservationTickets.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }
    void updateGenInfo(){
        final Dialog dialog = new Dialog(ManageBoardingHouse.this);

        final EditText price,available,roomcapacity,description;
        final CheckBox waterBill,curretBill;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_general_information);

        Window window = dialog.getWindow();
        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        price = (EditText) dialog.findViewById(R.id.contactNumber);
        available = (EditText) dialog.findViewById(R.id.availableSpace);
        roomcapacity = (EditText) dialog.findViewById(R.id.roomCapacity);
        curretBill = (CheckBox) dialog.findViewById(R.id.currentBill);
        waterBill = (CheckBox) dialog.findViewById(R.id.waterBill);
        description = (EditText) dialog.findViewById(R.id.desciption);

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.parseInt(price.getText().toString())>2000){
                    price.setText("2000");
                    price.setError("2000 is the limit");
                }
            }
        });

        available.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.parseInt(available.getText().toString())>10){
                    available.setText("10");
                    available.setError("10 is the limit");
                }
            }
        });
        FirebaseFirestore.getInstance().collection("generalInformation").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("generalInformation", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("generalInformation", "Current data: " + documentSnapshot.getData());
                    GeneralInformationObjectModel generalInformationObjectModel = documentSnapshot.toObject(GeneralInformationObjectModel.class);
                    try {
                        price.setText(generalInformationObjectModel.getPrice()+"");
                        available.setText(generalInformationObjectModel.getAvailable()+"");
                        roomcapacity.setText(generalInformationObjectModel.getRoomCapacity()+"");
                        waterBill.setChecked(generalInformationObjectModel.isWaterBil());
                        curretBill.setChecked(generalInformationObjectModel.isCurrentBill());
                        description.setText(generalInformationObjectModel.getDescription());
                    }catch (NullPointerException ex){

                    }
                } else {
                    Log.d("generalInformation", "Current data: null");
                }

            }
        });

        dialog.findViewById(R.id.saveChanges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GeneralInformationObjectModel generalInformationObjectModel =
                        new GeneralInformationObjectModel(auth.getUid(),
                                Integer.parseInt(price.getText().toString()),
                                Integer.parseInt(available.getText().toString()),
                                Integer.parseInt(roomcapacity.getText().toString()),
                                waterBill.isChecked(),
                                curretBill.isChecked(),
                                description.getText().toString(),"roomtype"
                                );

                FirebaseFirestore.getInstance()
                        .collection("generalInformation")
                        .document(auth.getUid()).set(generalInformationObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }

    public void performFileSearch() {
        getStoragePermission();
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data imageType.
        // If one wanted to search for ogg vorbis files, the imageType would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choose File"), READ_REQUEST_CODE);

    }
    private void getStoragePermission(){
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            access_storage = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    storagepermision_access_code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.getLastPathSegment());
//                ImageBannerOldURL = bannerUri;


                uploadItemBanner(uri);

            }else {
                System.out.println("null?");
            }
        }
    }
    private void setImage(Uri uri, ImageView imageView){
        // floatClearImage.setVisibility(View.VISIBLE);
        // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
        bannerUri = uri;
        imageSet = true;
//        GlideApp.with(AddRoom.this).load(uri).centerCrop().into(imageView);
        imageView.setPadding(0,0,0,0);
    }



    public void uploadItemBanner(final Uri ImageStorageURI){
//        setProgress(true);
//        loadingContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.loadingImage).setVisibility(View.VISIBLE);
        if (ImageStorageURI!=null) {
            InputStream storeBannerFile = null;
            try {
                storeBannerFile = getContentResolver().openInputStream(ImageStorageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final InputStream file = storeBannerFile;

            final StorageReference ImagestoreRef = FirebaseStorage.getInstance().getReference().child("images"+ File.separator+ File.separator + getFileName(ImageStorageURI)
                    +storeBannerFile.toString()+File.separator+getFileName(ImageStorageURI));
            ImagestoreRef.putStream(storeBannerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagestoreRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("imageBanner",uri.toString());
                            FirebaseFirestore.getInstance()
                                    .collection("imageBanner")
                                    .document(auth.getUid()).set(data);

//                            uploadPost(caption.getText().toString(),uri.toString());
//                            saveRoom(uri.toString());
//                            Utils.callToast(context,"Success");
//                            loadingContainer.setVisibility(View.GONE);
                            findViewById(R.id.loadingImage).setVisibility(View.GONE);


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    setProgress(false);
                    System.out.print(e);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    try {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/file.available());
//                        uploadProgress.setText("Uploading " +taskSnapshot.getBytesTransferred()+" of "+file.available()+" "+progress+" %");
                    }catch (IOException e){

                    }

                }
            });
        }

    }
    private String getFileName(Uri uri) {

        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    void setAppBarImage(){
        FirebaseFirestore.getInstance().collection("imageBanner").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot != null && documentSnapshot.exists()){
                    GlideApp.with(ManageBoardingHouse.this)
                            .load(documentSnapshot.get("imageBanner"))
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) findViewById(R.id.app_bar_image));
                }
            }
        });
    }





}
