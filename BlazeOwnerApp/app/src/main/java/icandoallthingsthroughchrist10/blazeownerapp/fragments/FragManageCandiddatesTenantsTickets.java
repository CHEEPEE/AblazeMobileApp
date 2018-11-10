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

import icandoallthingsthroughchrist10.blazeownerapp.AccountIsBlockOrPending;
import icandoallthingsthroughchrist10.blazeownerapp.ManageReservationTickets;
import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.AccountThatNotifiedModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GeneralInformationObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.views.CandidatesRecyclerViewAdapter;
import icandoallthingsthroughchrist10.blazeownerapp.views.TicketsRecyclerViewAdapter;

public class FragManageCandiddatesTenantsTickets extends Fragment {
    TextView addTicket;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;
    ArrayList<AccountThatNotifiedModel> accountThatNotifiedModels = new ArrayList<>();
    CandidatesRecyclerViewAdapter candidatesRecyclerViewAdapter;
    RecyclerView candidatesList;

    public FragManageCandiddatesTenantsTickets(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_candidate_reservations, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        candidatesList = (RecyclerView) view.findViewById(R.id.candidatesList);
        candidatesRecyclerViewAdapter = new CandidatesRecyclerViewAdapter(getContext(),accountThatNotifiedModels);
        candidatesList.setAdapter(candidatesRecyclerViewAdapter);
        candidatesList.setLayoutManager(new LinearLayoutManager(getContext()));
        getCandidates();
        return view;
    }

    void getCandidates(){
        db.collection("candidates").whereEqualTo("ownerAccountId",uid).whereEqualTo("status",false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
            accountThatNotifiedModels.clear();
            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                AccountThatNotifiedModel accountThatNotifiedModel = documentSnapshot.toObject(AccountThatNotifiedModel.class);
                accountThatNotifiedModels.add(accountThatNotifiedModel);
            }
            candidatesRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }


}
