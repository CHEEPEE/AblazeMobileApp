package com.blaze.ablazetenants.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.appModule.GlideApp;
import com.blaze.ablazetenants.objectModels.BoardingHouseProfileObjectModel;
import com.blaze.ablazetenants.objectModels.GeneralInformationObjectModel;
import com.blaze.ablazetenants.objectModels.ReservationTicketObjectModel;
import com.blaze.ablazetenants.views.BoardingHouseListRecyclerViewAdapter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BoardingHouseListRecyclerViewAdapter bHadapter;
    ArrayList<GeneralInformationObjectModel> generalInformationObjectModelArrayList = new ArrayList<>();
    RecyclerView boardingHouseRecyclerView;
    EditText searchBar;
    ImageView ticketStatus;
    ArrayList<BoardingHouseProfileObjectModel> houseProfileObjectModels = new ArrayList<>();
    ReservationTicketObjectModel reservationTicketObjectModel;
    int maxPrice = 0;
    int minSpace = 0;
    FirebaseAuth mAuth;
    ImageView account_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        searchBar = (EditText) findViewById(R.id.searchBar);
        account_settings = (ImageView) findViewById(R.id.account_settings);
        ticketStatus = (ImageView) findViewById(R.id.ticketStatus);
        boardingHouseRecyclerView = (RecyclerView)findViewById(R.id.boardingHouseRecyclerView);
        ticketStatus = (ImageView) findViewById(R.id.ticketStatus);
        boardingHouseRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        bHadapter =new BoardingHouseListRecyclerViewAdapter(MainActivity.this,generalInformationObjectModelArrayList);
        boardingHouseRecyclerView.setAdapter(bHadapter);
        getBoardingHouse();
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoardingHouseWithKeyWord(searchBar.getText().toString());
            }
        });
        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
        account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog();
            }
        });

        db.collection("reservationTickets").whereEqualTo("tenantId",mAuth.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    reservationTicketObjectModel = documentSnapshot.toObject(ReservationTicketObjectModel.class);
                    if (reservationTicketObjectModel.getStatus().equals("reserved")){
                        ticketStatus.setColorFilter(getResources().getColor(R.color.colorGreen));
                    }
                }
            }
        });
        ticketStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationTicketObjectModel != null){
                    getTicketDetails();
                }

            }
        });
    }
    void getBoardingHouse(){
        Query query;
        if (maxPrice ==0 && minSpace == 0){
            query = db.collection("generalInformation")
                    .orderBy("price", Query.Direction.ASCENDING);
        }

        else if (maxPrice == 0 && minSpace != 0){
            query = db.collection("generalInformation")
                    .whereGreaterThanOrEqualTo("available", minSpace)
                    .orderBy("available", Query.Direction.ASCENDING);
        }
        else if (maxPrice != 0 && minSpace == 0){
            query = db.collection("generalInformation")
                    .whereLessThanOrEqualTo("price",maxPrice)
                    .orderBy("price", Query.Direction.ASCENDING);
        }else {
            query = db.collection("generalInformation")
                    .whereLessThanOrEqualTo("price",maxPrice);
            query.whereGreaterThan("available", minSpace-1).orderBy("available", Query.Direction.ASCENDING);
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                generalInformationObjectModelArrayList.clear();
                houseProfileObjectModels.clear();
               try {
                   for (final DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                       System.out.println(document.getData().get("userId").toString());
                       db.collection("houseProfiles")
                               .document(document.getData().get("userId").toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                           @Override
                           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                               try {
//                                   System.out.println(documentSnapshot.getData());
                                   if (documentSnapshot.getData().get("status").toString().equals("active")){
                                       houseProfileObjectModels.add(documentSnapshot.toObject(BoardingHouseProfileObjectModel.class));
                                       generalInformationObjectModelArrayList.add(document.toObject(GeneralInformationObjectModel.class));
                                       bHadapter.notifyDataSetChanged();
                                   }
                               }catch (NullPointerException ex){

                               }

                           }
                       });
                   }
               }catch (NullPointerException ex){

               }

            }
        });
    }


    void getTicketDetails(){

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_ticket_details);

        Window window = dialog.getWindow();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final CircleImageView bhouseIcon = (CircleImageView) dialog.findViewById(R.id.boardingHouseIcon);
        final TextView bhouseName = (TextView) dialog.findViewById(R.id.bhouseName);
        db.collection("imageBanner").document(reservationTicketObjectModel.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                GlideApp.with(MainActivity.this).load(documentSnapshot.get("imageBanner").toString()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(bhouseIcon);
            }
        });
        db.collection("houseProfiles").document(reservationTicketObjectModel.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                BoardingHouseProfileObjectModel boardingHouseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
                bhouseName.setText(boardingHouseProfileObjectModel.getName());
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    void getBoardingHouseWithKeyWord(final String keyword){
        generalInformationObjectModelArrayList.clear();
        for (BoardingHouseProfileObjectModel houseProfileObjectModel:houseProfileObjectModels){
            if (houseProfileObjectModel.getName().toLowerCase().contains(keyword.toLowerCase())){
                db.collection("generalInformation").document(houseProfileObjectModel.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        generalInformationObjectModelArrayList.add(documentSnapshot.toObject(GeneralInformationObjectModel.class));
                        bHadapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    void filterDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_filter);

        Window window = dialog.getWindow();
        dialog.show();
        dialog.findViewById(R.id.filterPrice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoardingHouseFilter("price");
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.filterSpace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoardingHouseFilter("available");
                dialog.dismiss();
            }
        });
    }

    void getBoardingHouseFilter(String filterType){
        Query query;
        query = db.collection("generalInformation")
                .orderBy(filterType, filterType.equals("available")?Query.Direction.DESCENDING:Query.Direction.ASCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                generalInformationObjectModelArrayList.clear();
                houseProfileObjectModels.clear();
                try {
                    for (final DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                       System.out.println(document.getData().get("userId").toString());
                        db.collection("houseProfiles")
                                .document(document.getData().get("userId").toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                try {
//                                   System.out.println(documentSnapshot.getData());
                                    if (documentSnapshot.getData().get("status").toString().equals("active")){
                                        houseProfileObjectModels.add(documentSnapshot.toObject(BoardingHouseProfileObjectModel.class));
                                        generalInformationObjectModelArrayList.add(document.toObject(GeneralInformationObjectModel.class));
                                        bHadapter.notifyDataSetChanged();
                                    }
                                }catch (NullPointerException ex){

                                }

                            }
                        });
                    }
                }catch (NullPointerException ex){

                }

            }
        });
    }

    void settingDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_account_menu);
        Window window = dialog.getWindow();
        dialog.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClibBoard();
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
    void copyToClibBoard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("userId", FirebaseAuth.getInstance().getUid());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this,"Copy to Clipboard "+FirebaseAuth.getInstance().getUid(),Toast.LENGTH_SHORT).show();
    }
}
