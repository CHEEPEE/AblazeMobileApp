package icandoallthingsthroughchrist10.blazeownerapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import icandoallthingsthroughchrist10.blazeownerapp.GlideApp;
import icandoallthingsthroughchrist10.blazeownerapp.R;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.GalleryObjectModel;
import icandoallthingsthroughchrist10.blazeownerapp.objectModel.ReservationTicketObjectModel;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class TicketsRecyclerViewAdapter
        extends RecyclerView.Adapter<TicketsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ReservationTicketObjectModel> reservationTicketObjectModels = new ArrayList<>();
    private Context context;
    private String categoryKey;
    private String businessKey;

    public class MyViewHolder extends RecyclerView.ViewHolder{


        public MyViewHolder(View view){
            super(view);

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

    private void addMenuDialog(){

    }
}


