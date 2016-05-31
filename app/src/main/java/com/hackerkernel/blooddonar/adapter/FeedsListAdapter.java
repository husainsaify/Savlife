package com.hackerkernel.blooddonar.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.pojo.FeedsListPojo;
import com.hackerkernel.blooddonar.util.Util;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter class for feeds list
 */
public class FeedsListAdapter extends RecyclerView.Adapter<FeedsListAdapter.FeedsViewHolder>{
    private List<FeedsListPojo> list;
    private LayoutInflater inflater;
    private Context context;

    public FeedsListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<FeedsListPojo> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feeds_list_row,parent,false);
        return new FeedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedsViewHolder holder, int position) {
        FeedsListPojo pojo = list.get(position);

        if (pojo.getType().equals("0")){
            //its a status feed
            holder.mFeedPhoto.setVisibility(View.GONE);
        } else{
            //its a image status feed
            //download image
            String feedImage = EndPoints.IMAGE_BASE_URL + pojo.getImage();
            Glide.with(context)
                    .load(feedImage)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.placeholder_300_300)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mFeedPhoto);
            holder.mFeedPhoto.setVisibility(View.VISIBLE);
        }
        holder.mFeedStatus.setText(pojo.getStatus());
        holder.mUserFullNAme.setText(pojo.getUserFullname());
        String time = Util.getTimeAgo(Long.parseLong(pojo.getTimestamp()));
        holder.mTimeAgo.setText(time);
        //load user image
        String userImage = EndPoints.IMAGE_BASE_URL + pojo.getUserImage();
        Glide.with(context)
                .load(userImage)
                .placeholder(R.drawable.placeholder_80_80)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FeedsViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.feed_user_image) ImageView userImage;
        @Bind(R.id.feed_user_name) TextView mUserFullNAme;
        @Bind(R.id.feed_time_ago) TextView mTimeAgo;
        @Bind(R.id.feed_photo) ImageView mFeedPhoto;
        @Bind(R.id.feed_status) TextView mFeedStatus;

        public FeedsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
