package com.blaze.ablazetenants.views;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.activity.ViewBoardingHouse;
import com.blaze.ablazetenants.appModule.GlideApp;
import com.blaze.ablazetenants.objectModels.BoardingHouseProfileObjectModel;
import com.blaze.ablazetenants.objectModels.GeneralInformationObjectModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class BoardingHouseListRecyclerViewAdapter extends RecyclerView.Adapter<BoardingHouseListRecyclerViewAdapter.MyViewHolder> {
   private ArrayList<GeneralInformationObjectModel> generalInformationObjectModelArrayList = new ArrayList<>();
   private Context context;
   int active = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder{
    TextView bHouseName,price,space,viewBHouse,location;
    ImageView imageBanner;
    ConstraintLayout container;



        public MyViewHolder(View view){
            super(view);
            bHouseName = (TextView) view.findViewById(R.id.bHouseName);
            imageBanner = (ImageView) view.findViewById(R.id.imageBanner);
            price = (TextView) view.findViewById(R.id.price);
            space = (TextView) view.findViewById(R.id.spaceAvaliable);
            container = (ConstraintLayout) view.findViewById(R.id.container);
            location = (TextView) view.findViewById(R.id.location);
//            viewBHouse = (TextView) view.findViewById(R.id.viewBHouse);
        }
    }

    public BoardingHouseListRecyclerViewAdapter(Context c, ArrayList<GeneralInformationObjectModel> generalInformationObjectModels){
    this.context = c;
    this.generalInformationObjectModelArrayList = generalInformationObjectModels;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boarding_house,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GeneralInformationObjectModel generalInformationObjectModel = generalInformationObjectModelArrayList.get(position);


        holder.price.setText("₱ "+generalInformationObjectModel.getPrice()+"");
        holder.space.setText("Space Available: "+generalInformationObjectModel.getAvailable()+"");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewBoardingHouse.class);
                i.putExtra("key",generalInformationObjectModel.getUserId());
                context.startActivity(i);
            }
        });

        FirebaseFirestore.getInstance().collection("houseProfiles")
                .document(generalInformationObjectModel.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                 BoardingHouseProfileObjectModel houseProfileObjectModel = documentSnapshot.toObject(BoardingHouseProfileObjectModel.class);
                 holder.bHouseName.setText(houseProfileObjectModel.getName());
                 holder.location.setText(houseProfileObjectModel.getAddress());

            }
        });
        FirebaseFirestore.getInstance()
                .collection("imageBanner")
                .document(generalInformationObjectModel.getUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        try {
                            GlideApp.with(context)
                                    .load(documentSnapshot.getData().get("imageBanner"))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                                    .placeholder(R.drawable.ic_home_black_24dp)
                                    .into(holder.imageBanner);
                        }catch (NullPointerException ex){
                            GlideApp.with(context)
                                    .load("")
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                                    .placeholder(R.drawable.ic_home_black_24dp)
                                    .into(holder.imageBanner);
                        }
                    }
                });
    }
    @Override
    public int getItemCount() {
        return generalInformationObjectModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, BoardingHouseProfileObjectModel boardingHouse);

    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}


