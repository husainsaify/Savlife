package com.hackerkernel.blooddonar.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.hackerkernel) TextView mTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.about);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set footer text
        mTextview.append("Made with ");
        Spanned redHeart = Html.fromHtml("<font color='red'>&#10084;</font>");
        mTextview.append(redHeart);
        mTextview.append(" By HackerKernel.com");
        mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hackerkernel.com/refer.php?id=savlife"));
                startActivity(browserIntent);
            }
        });
    }
}
