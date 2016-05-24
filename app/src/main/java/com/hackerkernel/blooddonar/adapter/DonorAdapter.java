package com.hackerkernel.blooddonar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.pojo.DonorListPojo;

import java.util.List;

/**
 * Created by QUT on 5/23/2016.
 */
public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {
    private List<DonorListPojo> donorList;
    private ImageLoader mImageLoader;


    public DonorAdapter(List<DonorListPojo> donorList) {
        this.donorList = donorList;
        this.mImageLoader = MyVolley.getInstance().getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donour_listrow, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DonorListPojo pojo = new DonorListPojo();
        holder.userName.setText(pojo.getUserName());
        holder.bloodGroup.setText(pojo.getUserBloodGroup());

        mImageLoader.get(pojo.getImageUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.userImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, bloodGroup;
        private ImageView userImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.donor_name);
            bloodGroup = (TextView) itemView.findViewById(R.id.blood_group_text);
            userImage = (ImageView) itemView.findViewById(R.id.donor_image);

        }

    }
}

