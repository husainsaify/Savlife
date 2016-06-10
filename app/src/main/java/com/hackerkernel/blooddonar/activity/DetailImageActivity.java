package com.hackerkernel.blooddonar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailImageActivity extends AppCompatActivity {
    @Bind(R.id.imageview) ImageView mImageView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Image Detail");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = getIntent().getExtras().getString(Constants.COM_IMG);

        //download image
        String imageUrl = EndPoints.IMAGE_BASE_URL + url;
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_300_300)
                .into(mImageView);
    }
}
