package icandoallthingsthroughchrist10.blazeownerapp.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import icandoallthingsthroughchrist10.blazeownerapp.GlideApp;
import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.BoardingHouseProfileObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GalleryObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.UserProfileObjectModel;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class TicketsRecyclerViewAdapter
        extends RecyclerView.Adapter<TicketsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ReservationTicketObjectModel> reservationTicketObjectModels = new ArrayList<>();
    private Context context;
    private String category;
    private String businessKey;

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

    public TicketsRecyclerViewAdapter(Context c, ArrayList<ReservationTicketObjectModel> galleryDataModels){
        this.context = c;
        this.reservationTicketObjectModels = galleryDataModels;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation_ticket,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ReservationTicketObjectModel reservationTicketObjectModel = reservationTicketObjectModels.get(position);
        holder.setTenantStatus.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_block_black_24dp),null,null,null);
        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketDialog(reservationTicketObjectModel);
            }
        });
        holder.setTenantStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationTicketObjectModel.getStatus().equals("reserved")){
                    approve(reservationTicketObjectModel);
                }else if(reservationTicketObjectModel.getStatus().equals("approved")){
                    blockTenant(reservationTicketObjectModel);
                }else if (reservationTicketObjectModel.getStatus().equals("blocked")){
                    approve(reservationTicketObjectModel);
                }

            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReservation(reservationTicketObjectModel);
            }
        });
        holder.setTenantStatus.setText(reservationTicketObjectModel.getStatus().equals("approved")?"Block Tenant":"Approve Tenant");
        if (reservationTicketObjectModel.getStatus().equals("approved")){
            holder.cancel.setVisibility(View.INVISIBLE);
        }else if(reservationTicketObjectModel.getStatus().equals("canceled")){
            holder.cancel.setVisibility(View.INVISIBLE);

        }else if (reservationTicketObjectModel.getStatus().equals("blocked")){
            holder.cancel.setVisibility(View.INVISIBLE);
        }
        else {
            holder.cancel.setVisibility(View.VISIBLE);
        }
        holder.status.setText(reservationTicketObjectModel.getStatus());
        try {
            FirebaseFirestore.getInstance().collection("users")
                    .document(reservationTicketObjectModel.getTenantId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                    holder.tenantName.setText(userProfileObjectModel.getUserName());
                    GlideApp.with(context).load(userProfileObjectModel.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.circleImageView);
                }
            });
        }catch (IllegalArgumentException e){

        }
    }
    @Override
    public int getItemCount() {
        return reservationTicketObjectModels.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, GalleryObjectModel galleryDataModel);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private void ticketDialog(ReservationTicketObjectModel reservationTicketObjectModel){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_ticket_details);

        Window window = dialog.getWindow();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final CircleImageView bhouseIcon = (CircleImageView) dialog.findViewById(R.id.boardingHouseIcon);
        final TextView bhouseName = (TextView) dialog.findViewById(R.id.bhouseName);
        final TextView name = (TextView) dialog.findViewById(R.id.name);
        TextView date = (TextView) dialog.findViewById(R.id.date);

        FirebaseFirestore.getInstance().collection( "imageBanner").document(reservationTicketObjectModel.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

            }
        });
        FirebaseFirestore.getInstance().collection("houseProfiles").document(reservationTicketObjectModel.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                BoardingHouseProfileObjectModel boardingHouseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);

                bhouseName.setText(boardingHouseProfileObjectModel.getName());
            }
        });
        FirebaseFirestore.getInstance().collection("users").document(reservationTicketObjectModel.getTenantId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name.setText(documentSnapshot.get("userName").toString());
                bhouseName.setText(documentSnapshot.get("userName").toString());
                GlideApp.with(context).load(documentSnapshot.get("userImage").toString()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(bhouseIcon);
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView status = (TextView) dialog.findViewById(R.id.status);
        status.setText(reservationTicketObjectModel.getStatus());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd hh:mm aaa");
        date.setText(format.format(reservationTicketObjectModel.getTimeStamp()) +"");
    }

    private void approve(final ReservationTicketObjectModel reservationTicketObjectModel){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_approve_tenant);
        Window window = dialog.getWindow();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView approve = (TextView) dialog.findViewById(R.id.approve);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
       approve.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              FirebaseFirestore.getInstance()
                      .collection("reservationTickets").document(reservationTicketObjectModel.getTransactionCode())
                      .update("status","approved").addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(context,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                  }
              }).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                  }
              });
          }
      });
    }

    private void blockTenant(final ReservationTicketObjectModel reservationTicketObjectModel){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_block_tenant);
        Window window = dialog.getWindow();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView block = (TextView) dialog.findViewById(R.id.block);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance()
                        .collection("reservationTickets").document(reservationTicketObjectModel.getTransactionCode())
                        .update("status","blocked").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void cancelReservation(final ReservationTicketObjectModel reservationTicketObjectModel){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_cancel_reservation_tenant);
        Window window = dialog.getWindow();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView cancelReservation = (TextView) dialog.findViewById(R.id.cancelReservation);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance()
                        .collection("reservationTickets").document(reservationTicketObjectModel.getTransactionCode())
                        .update("status","canceled").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }



}


