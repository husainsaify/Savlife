package com.hackerkernel.blooddonar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.DetailImageActivity;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.FeedsListPojo;
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter class for feeds list
 */
public class FeedsListAdapter extends RecyclerView.Adapter<FeedsListAdapter.FeedsViewHolder>{
    private static final String TAG = FeedsListAdapter.class.getSimpleName();
    private List<FeedsListPojo> list;
    private LayoutInflater inflater;
    private Activity context;
    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;

    public FeedsListAdapter(Activity activity){
        this.context = activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //init volley & sp
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(activity);
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
        holder.mFeedSmileCounter.setText(pojo.getLikes()+" Likes");

        //Set appropriate smile on the basis of userliked this pic or not
        if (pojo.getUserLikedFeed()){
            holder.mFeedSmileIcon.setImageResource(R.drawable.ic_like_color);
        }else {
            holder.mFeedSmileIcon.setImageResource(R.drawable.ic_like_grey);
        }

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
        @Bind(R.id.feed_smile_counter) TextView mFeedSmileCounter;
        @Bind(R.id.feed_smile_icon) ImageView mFeedSmileIcon;

        public FeedsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            mFeedPhoto.setOnClickListener(this);

            //when someone click on smile counter or even simle
            mFeedSmileCounter.setOnClickListener(this);
            mFeedSmileIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.feed_photo:
                        int pos = getAdapterPosition();
                        String link = list.get(pos).getImage();
                        Intent intent = new Intent(context, DetailImageActivity.class);
                        intent.putExtra(Constants.COM_IMG,link);
                        context.startActivity(intent);
                    break;
                case R.id.feed_smile_icon:
                        registerSmile();
                    break;
                case R.id.feed_smile_counter:
                        registerSmile();
                    break;
            }

        }

        private void registerSmile() {
            //change icon
            mFeedSmileIcon.setImageResource(R.drawable.ic_like_color);
            //get feed id
            int pos = getAdapterPosition();
            String feedId = list.get(pos).getFeedId();
            registerLikeInBackground(feedId,mFeedSmileIcon,mFeedSmileCounter);
        }
    }

    private void registerLikeInBackground(final String feedId, final ImageView mFeedSmileIcon, final TextView mFeedSmileCounter) {
        if (Util.isNetworkAvailable()){
            StringRequest request = new StringRequest(Request.Method.POST, EndPoints.ADD_LIKES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SimplePojo c = JsonParser.SimpleParser(response);
                        if (c.isReturned()){
                            //update the number of likes
                            String oldLike = mFeedSmileCounter.getText().toString();
                            String[] likeArray = oldLike.split("L");
                            String oldLikeNumber = likeArray[0].trim();
                            int newLike = Integer.parseInt(oldLikeNumber) + 1;
                            mFeedSmileCounter.setText(newLike+" Likes");
                        }else {
                            Toast.makeText(context,c.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG,"HUS: registerLikeInBackground: "+e.getMessage());
                        //change icon
                        mFeedSmileIcon.setImageResource(R.drawable.ic_like_grey);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG,"HUS: registerLikeInBackground: "+error.getMessage());
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                    params.put(Constants.COM_MOBILE,sp.getUserMobile());
                    params.put(Constants.COM_ID,feedId);
                    return params;
                }
            };
            mRequestQueue.add(request);
        }
    }


}
