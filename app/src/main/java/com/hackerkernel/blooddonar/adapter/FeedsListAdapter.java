package com.hackerkernel.blooddonar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.DetailImageActivity;
import com.hackerkernel.blooddonar.constant.Constants;
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
    private Activity context;

    public FeedsListAdapter(Activity activity){
        this.context = activity;
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
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mFeedPhoto);
            holder.mFeedPhoto.setVisibility(View.VISIBLE);
        }
        holder.mFeedStatus.setText(pojo.getStatus());
        holder.mUserFullNAme.setText(pojo.getUserFullname());
        String time = Util.getTimeAgo(Long.parseLong(pojo.getTimestamp()));
        holder.mTimeAgo.setText(time);

        //load user image
        if (!pojo.getUserImage().isEmpty()){
            String userImage = EndPoints.IMAGE_BASE_URL + pojo.getUserImage();
            Glide.with(context)
                    .load(userImage)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.userImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FeedsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.feed_user_image) ImageView userImage;
        @Bind(R.id.feed_user_name) TextView mUserFullNAme;
        @Bind(R.id.feed_time_ago) TextView mTimeAgo;
        @Bind(R.id.feed_photo) ImageView mFeedPhoto;
        @Bind(R.id.feed_status) TextView mFeedStatus;

        public FeedsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            mFeedPhoto.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            String link = list.get(pos).getImage();
            Intent intent = new Intent(context, DetailImageActivity.class);
            intent.putExtra(Constants.COM_IMG,link);
            context.startActivity(intent);
        }
    }
}
