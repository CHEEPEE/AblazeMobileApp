package icandoallthingsthroughchrist10.blazeownerapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.util.ArrayList;

import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GalleryObjectModel;

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



        }
    }

    public GalleryRecyclerViewAdapter(Context c, ArrayList<GalleryObjectModel> galleryDataModels, String businessKey){
        this.context = c;
        this.galleryDataModelArrayList = galleryDataModels;
        this.businessKey = businessKey;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gallery,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GalleryObjectModel galleryDataModel = galleryDataModelArrayList.get(position);

//          try{
//              if (galleryDataModel.getKey().equals("nokey")){
//                  holder.addImage.setVisibility(View.VISIBLE);
//                  holder.addImage.setOnClickListener(new View.OnClickListener() {
//                      @Override
//                      public void onClick(View v) {
//                          mOnItemClickLitener.onItemClick(v,position,galleryDataModel);
//                      }
//                  });
//              }else {
//                  holder.addImage.setVisibility(View.GONE);
//                  GlideApp.with(context).load(galleryDataModel.getImagePath()).centerCrop().into(holder.galleryImage);
//              }
//          }catch (NullPointerException e){
//
//          }

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


