package com.blaze.ablazetenants.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blaze.ablazetenants.R;
import com.blaze.ablazetenants.appModule.GlideApp;
import com.blaze.ablazetenants.objectModels.GalleryObjectModel;

import java.util.ArrayList;



/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class GalleryRecyclerViewAdapter
        extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<GalleryObjectModel> galleryDataModelArrayList = new ArrayList<>();
    private Context context;
    private String categoryKey;
    private String businessKey;

    public class MyViewHolder extends RecyclerView.ViewHolder{
      public ImageView addImage;
      public ImageView galleryImage;

        public MyViewHolder(View view){
            super(view);
            galleryImage = (ImageView) view.findViewById(R.id.image);

        }
    }

    public GalleryRecyclerViewAdapter(Context c, ArrayList<GalleryObjectModel> galleryDataModels, String businessKey){
        this.context = c;
        this.galleryDataModelArrayList = galleryDataModels;
        this.businessKey = businessKey;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gallery_v2,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GalleryObjectModel galleryDataModel = galleryDataModelArrayList.get(position);
        GlideApp.with(context).load(galleryDataModel.getImageUrl()).centerCrop().into(holder.galleryImage);

    }

    @Override
    public int getItemCount() {
        return galleryDataModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, GalleryObjectModel galleryDataModel);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private void addMenuDialog(){

    }
}


