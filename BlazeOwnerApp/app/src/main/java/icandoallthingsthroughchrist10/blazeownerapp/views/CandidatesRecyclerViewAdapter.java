package icandoallthingsthroughchrist10.blazeownerapp.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import icandoallthingsthroughchrist10.blazeownerapp.GlideApp;
import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.AccountThatNotifiedModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.BoardingHouseProfileObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GalleryObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GeneralInformationObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.UserProfileObjectModel;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class CandidatesRecyclerViewAdapter
        extends RecyclerView.Adapter<CandidatesRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<AccountThatNotifiedModel> accountThatNotifiedModels = new ArrayList<>();
    private Context context;
    private String category;
    private String businessKey;
    private FirebaseFirestore db;
    private String uid;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tenantName,viewDetails, setTenantStatus,status,cancel;
        CircleImageView circleImageView;
        public MyViewHolder(View view){
            super(view);
            circleImageView = (CircleImageView) view.findViewById(R.id.circleImageView);
            tenantName = (TextView) view.findViewById(R.id.tenantName);
            viewDetails = (TextView) view.findViewById(R.id.viewDetails);
            setTenantStatus = (TextView) view.findViewById(R.id.blocTenant);
            status = (TextView) view.findViewById(R.id.status);
            cancel = (TextView) view.findViewById(R.id.cancel);
        }
    }

    public CandidatesRecyclerViewAdapter(Context c, ArrayList<AccountThatNotifiedModel> galleryDataModels){
        this.context = c;
        this.accountThatNotifiedModels = galleryDataModels;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_cadidates_reservation,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AccountThatNotifiedModel accountThatNotifiedModel = accountThatNotifiedModels.get(position);
        uid = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.getInstance().collection("users")
                .document(accountThatNotifiedModel.getTenantAccountId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                holder.tenantName.setText(userProfileObjectModel.getUserName());
            }
        });
        holder.setTenantStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTicket(accountThatNotifiedModel.getTenantAccountId(),accountThatNotifiedModel.getKey());
            }
        });
        holder.setTenantStatus.setText("Approve Reservation");
        holder.status.setText("");

    }
    @Override
    public int getItemCount() {
        return accountThatNotifiedModels.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, GalleryObjectModel galleryDataModel);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    void addTicket(String accountId, final String key){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_add_ticket);
        final EditText accountCode = (EditText) dialog.findViewById(R.id.account_code);
        final EditText additionalInfo = (EditText) dialog.findViewById(R.id.additionInfo);
        final TextView saveAccountCode = (TextView) dialog.findViewById(R.id.saveAccountCode);
        accountCode.setText(accountId);
        accountCode.setVisibility(View.INVISIBLE);
        saveAccountCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountCode.getText().toString().trim().length()!=0){
                    saveAccountCode(accountCode.getText().toString(),dialog,additionalInfo.getText().toString().equals(null)?"N/A":additionalInfo.getText().toString(),key);
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
    void saveAccountCode(final String accountCode, final Dialog dialog, String additionalInfo, final String candKey){
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
                            .whereEqualTo("ownerId", FirebaseAuth.getInstance().getUid())
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
                                        db.collection("candidates").document(candKey).update("status",true);
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


