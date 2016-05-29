package com.hackerkernel.blooddonar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.DealsDetailActivity;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.pojo.DealsListPojo;

import java.util.List;

/**
 * Adapter class for images
 */
public class DealsListAdapter extends RecyclerView.Adapter<DealsListAdapter.MyViewHolders> {
    private List<DealsListPojo> mList;
    private Context mContext;
    public DealsListAdapter (Context context){
        this.mContext = context;
    }
    public  void setList(List<DealsListPojo> list){
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
        DealsListPojo pojo = mList.get(position);
        holder.deals.setText(pojo.getDeal());
        holder.deals.append("% off");
        holder.hospitalName.setText(pojo.getHospitalName());
        holder.description.setText(pojo.getDescription());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView hospitalName, description, deals;
        private ImageView hospitalImage;
        public MyViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            hospitalImage = (ImageView) itemView.findViewById(R.id.deals_image);
            hospitalName = (TextView) itemView.findViewById(R.id.deals_hospital_name);
            description = (TextView) itemView.findViewById(R.id.deals_description);
            deals = (TextView) itemView.findViewById(R.id.deals_off);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            String id = mList.get(pos).getDealsId();
            Intent intent = new Intent(mContext, DealsDetailActivity.class);
            intent.putExtra(Constants.COM_ID,id);
            mContext.startActivity(intent);
        }
    }
}
