package com.hackerkernel.blooddonar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.DonorDetailActivity;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.pojo.DonorListPojo;

import java.util.List;

/**
 * Created by QUT on 5/23/2016.
 */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {
    private static final String TAG = DonorAdapter.class.getSimpleName();
    private List<DonorListPojo> mList;
    private ImageLoader mImageLoader;
    private Context context;


    public DonorAdapter(Context context) {
        this.context = context;
        this.mImageLoader = MyVolley.getInstance().getImageLoader();
    }

    public void setList(List<DonorListPojo> list){
        this.mList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.donor_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DonorListPojo pojo = mList.get(position);
        holder.userName.setText(pojo.getUserName());
        holder.bloodGroup.setText(pojo.getUserBloodGroup());

        /*mImageLoader.get(pojo.getImageUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.userImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mList.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView userName, bloodGroup;
        private ImageView userImage;


        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            userName = (TextView) itemView.findViewById(R.id.donor_name);
            bloodGroup = (TextView) itemView.findViewById(R.id.blood_group_text);
            userImage = (ImageView) itemView.findViewById(R.id.donor_image);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Toast.makeText(context,pos+"",Toast.LENGTH_LONG).show();
            String id = mList.get(pos).getUserId();
            Intent intent = new Intent(context, DonorDetailActivity.class);
            intent.putExtra(Constants.COM_ID,id);
            context.startActivity(intent);
        }
    }
}

