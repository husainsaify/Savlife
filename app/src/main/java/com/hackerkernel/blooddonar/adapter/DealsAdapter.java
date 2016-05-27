package com.hackerkernel.blooddonar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.pojo.DealsPjo;

import java.util.List;

/**
 * Created by Murtaza on 5/27/2016.
 */
public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.MyViewHolders> {
  private List<DealsPjo> mList;
    private ImageLoader imageLoader;
    private Context mContext;
    public DealsAdapter (Context context){
        this.mContext = context;
        imageLoader = MyVolley.getInstance().getImageLoader();

    }
    public  void setmList(List<DealsPjo> list){
        this.mList = list;
        this.notifyDataSetChanged();
    }
    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.deals_list_row,parent,false);
        return new MyViewHolders(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolders holder, int position) {
        DealsPjo pojo = mList.get(position);
        holder.deals.setText(pojo.getDeal());
        holder.hospitalName.setText(pojo.getHospitalName());
        holder.description.setText(pojo.getDescription());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolders extends RecyclerView.ViewHolder {
        private TextView hospitalName, description, deals;
        private ImageView hospitalImage;
        public MyViewHolders(View itemView) {
            super(itemView);

            hospitalImage = (ImageView) itemView.findViewById(R.id.deals_image);
            hospitalName = (TextView) itemView.findViewById(R.id.deals_hospital_name);
            description = (TextView) itemView.findViewById(R.id.deals_description);
            deals = (TextView) itemView.findViewById(R.id.deals_offer);

        }
    }
}
