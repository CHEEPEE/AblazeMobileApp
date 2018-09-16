package com.blaze.ablazetenants.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.objectModels.BoardingHouseProfileObjectModel;
import com.blaze.ablazetenants.views.BoardingHouseListRecyclerViewAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BoardingHouseListRecyclerViewAdapter bHadapter;
    ArrayList<BoardingHouseProfileObjectModel> bhouseArrayList = new ArrayList<>();
    RecyclerView boardingHouseRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        boardingHouseRecyclerView = (RecyclerView)findViewById(R.id.boardingHouseRecyclerView);
        boardingHouseRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        bHadapter =new BoardingHouseListRecyclerViewAdapter(MainActivity.this,bhouseArrayList);
        boardingHouseRecyclerView.setAdapter(bHadapter);
        getBoardingHouse();

    }

    void getBoardingHouse(){
       db.collection("houseProfiles")
                .whereEqualTo("status", "active")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                bhouseArrayList.clear();
               try {
                   for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                       bhouseArrayList.add(document.toObject(BoardingHouseProfileObjectModel.class));
                   }
               }catch (NullPointerException ex){

               }
                bHadapter.notifyDataSetChanged();
            }
        });
    }
}
