package icandoallthingsthroughchrist10.blazeownerapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GeneralInformationObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.views.TicketsRecyclerViewAdapter;

public class ManageReservationTickets extends AppCompatActivity {
    TextView addTicket;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;
    TextView available;
    RecyclerView ticketList;
    Context context;
    ArrayList<ReservationTicketObjectModel> reservationTicketObjectModels = new ArrayList<>();
    TicketsRecyclerViewAdapter ticketsRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reservation_tickets);
        context = this;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        addTicket = (TextView) findViewById(R.id.addTicket);
        available = (TextView) findViewById(R.id.available);
        ticketList = (RecyclerView) findViewById(R.id.ticketList);
        addTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTicket();
            }
        });
        db.collection("generalInformation").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                GeneralInformationObjectModel generalInformationObjectModel = documentSnapshot.toObject(GeneralInformationObjectModel.class);
                available.setText(generalInformationObjectModel.getAvailable()+"");
            }
        });
        getReservationTickets();
    }
    void addTicket(){
        final Dialog dialog = new Dialog(ManageReservationTickets.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_add_ticket);
        final EditText accountCode = (EditText) dialog.findViewById(R.id.houseName);
        final EditText additionalInfo = (EditText) dialog.findViewById(R.id.additionInfo);
        final TextView saveAccountCode = (TextView) dialog.findViewById(R.id.saveAccountCode);
        saveAccountCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountCode.getText().toString().trim().length()!=0){
                    saveAccountCode(accountCode.getText().toString(),dialog,additionalInfo.getText().toString().equals(null)?"N/A":additionalInfo.getText().toString());
                    saveAccountCode.setClickable(false);
                }else {
                    accountCode.setError("Fill up Account Code");
                }
            }
        });
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    void getReservationTickets(){
        ticketsRecyclerViewAdapter = new TicketsRecyclerViewAdapter(context,reservationTicketObjectModels);
        ticketList.setLayoutManager(new LinearLayoutManager(context));
        ticketList.setAdapter(ticketsRecyclerViewAdapter);
        db.collection("reservationTickets")
                .whereEqualTo("ownerId",FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                reservationTicketObjectModels.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    reservationTicketObjectModels.add(documentSnapshot.toObject(ReservationTicketObjectModel.class));
                }
                ticketsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    void saveAccountCode(final String accountCode, final Dialog dialog,String additionalInfo){
        final String  key =  db.collection("ReservationTickets").document().getId();
        final  ReservationTicketObjectModel reservationTicketObjectModel = new ReservationTicketObjectModel(accountCode,
                uid,"null",key,additionalInfo,"reserved");

//        check available
        db.collection("generalInformation").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final GeneralInformationObjectModel generalInformationObjectModel = task.getResult().toObject(GeneralInformationObjectModel.class);
                if (generalInformationObjectModel.getAvailable()!=0){
//                    check if already inserted
                    db.collection("reservationTickets").whereEqualTo("tenantId",accountCode)
                            .whereEqualTo("ownerId",FirebaseAuth.getInstance().getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().getDocuments().size()==0){
//                    save ticket
                                db.collection("reservationTickets").document(key).set(reservationTicketObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                            decrement available
                                        db.collection("generalInformation").document(uid).update("available",generalInformationObjectModel.getAvailable()-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(context,"already Added",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(context,"No Space Available",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
}
