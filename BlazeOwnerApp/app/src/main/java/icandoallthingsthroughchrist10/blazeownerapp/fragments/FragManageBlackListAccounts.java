package icandoallthingsthroughchrist10.blazeownerapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GeneralInformationObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.views.TicketsRecyclerViewAdapter;

public class FragManageBlackListAccounts extends Fragment {
    TextView addTicket;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;
    TextView available;
    RecyclerView ticketList;
    Context context;
    ArrayList<ReservationTicketObjectModel> reservationTicketObjectModels = new ArrayList<>();
    TicketsRecyclerViewAdapter ticketsRecyclerViewAdapter;
    public FragManageBlackListAccounts(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manage_reservation_tickets, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        addTicket = (TextView) view.findViewById(R.id.addTicket);
        available = (TextView) view.findViewById(R.id.available);
        ticketList = (RecyclerView) view.findViewById(R.id.ticketList);
        addTicket.setVisibility(View.INVISIBLE);
        db.collection("generalInformation").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                GeneralInformationObjectModel generalInformationObjectModel = documentSnapshot.toObject(GeneralInformationObjectModel.class);
                available.setText(generalInformationObjectModel.getAvailable()+"");
            }
        });
        getReservationTickets();
        return view;
    }


    void getReservationTickets(){
        ticketsRecyclerViewAdapter = new TicketsRecyclerViewAdapter(getContext(),reservationTicketObjectModels);
        ticketList.setLayoutManager(new LinearLayoutManager(context));
        ticketList.setAdapter(ticketsRecyclerViewAdapter);
        db.collection("reservationTickets")
                .whereEqualTo("ownerId",FirebaseAuth.getInstance().getUid())
                .whereEqualTo("status","blocked")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        reservationTicketObjectModels.clear();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            reservationTicketObjectModels.add(documentSnapshot.toObject(ReservationTicketObjectModel.class));
                        }
                        ticketsRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
    }

}
