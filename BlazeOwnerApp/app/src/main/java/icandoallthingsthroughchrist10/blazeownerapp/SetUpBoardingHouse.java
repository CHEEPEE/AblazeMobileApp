package icandoallthingsthroughchrist10.blazeownerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import icandoallthingsthroughchrist10.blazeownerapp.objectModel.BoardingHouseProfileObjectModel;

public class SetUpBoardingHouse extends AppCompatActivity {
    EditText name,address,contact,email;
    TextView saveInfo;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_boarding_house);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        contact = (EditText) findViewById(R.id.rooms);
        saveInfo = (TextView) findViewById(R.id.saveInfo);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email.setText(auth.getCurrentUser().getEmail());
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBoardinghouseProfile();
            }
        });
    }
    void saveBoardinghouseProfile(){

        BoardingHouseProfileObjectModel boardingHouseProfileObjectModel =
                new BoardingHouseProfileObjectModel(auth.getUid(),
                        name.getText().toString(),
                        address.getText().toString(),
                        "Not Yet Set",
                        auth.getCurrentUser().getDisplayName(),
                        contact.getText().toString(),
                        email.getText().toString(),
                        "pending",null);
        db.collection("houseProfiles").document(auth.getUid())
                .set(boardingHouseProfileObjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(SetUpBoardingHouse.this,ManageBoardingHouse.class);
                        startActivity(i);
                        finish();
                    }
                });
    }
}
