package com.hackerkernel.blooddonar.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.pojo.FeedsListPojo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter class for feeds list
 */
public class FeedsListAdapter extends RecyclerView.Adapter<FeedsListAdapter.FeedsViewHolder>{
    private List<FeedsListPojo> list;
    private LayoutInflater inflater;

    public FeedsListAdapter(Context context){
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
            holder.mFeedPhoto.setVisibility(View.INVISIBLE);
            holder.mFeedStatus.setText(pojo.getStatus());
            holder.mUserFullNAme.setText(pojo.getUserFullname());
            holder.mTimeAgo.setText(pojo.getTimestamp());
           // holder.userImage.setImageBitmap();
        }
        else{
            holder.mFeedPhoto.setVisibility(View.VISIBLE);
            //holder.mFeedPhoto.setImageBitmap();
            holder.mFeedStatus.setText(pojo.getStatus());
            holder.mUserFullNAme.setText(pojo.getUserFullname());
            holder.mTimeAgo.setText(pojo.getTimestamp());
          //  holder.userImage.setImageBitmap();

        }

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
