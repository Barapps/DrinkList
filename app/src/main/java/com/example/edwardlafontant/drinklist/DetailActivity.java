package com.example.edwardlafontant.drinklist;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.edwardlafontant.drinklist.models.DrinkModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivDrinkIcon;
    private TextView tvDrink;
    private TextView tvTagline;
    private RatingBar rbDrinkRating;
    private TextView tvStory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Showing and Enabling clicks on the Home/Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        UIViews();

        //data from MainActivity, sent via intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("drinkModel");
            DrinkModel drinkModel = new Gson().fromJson(json, DrinkModel.class);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(drinkModel.getImage(), ivDrinkIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            tvDrink.setText(drinkModel.getDrink());
            tvTagline.setText(drinkModel.getTagline());

            // rating bar
            rbDrinkRating.setRating(drinkModel.getRating() / 2);
            tvStory.setText(drinkModel.getStory());

        }
    }
    private void UIViews(){

        ivDrinkIcon = (ImageView)findViewById(R.id.detIcon);
        tvDrink = (TextView)findViewById(R.id.tvDetDrink);
        tvTagline = (TextView)findViewById(R.id.tvDetTagline);
        tvStory = (TextView)findViewById(R.id.tvDetStory);
        rbDrinkRating = (RatingBar)findViewById(R.id.rbDetDrink);
        progressBar = (ProgressBar)findViewById(R.id.detprogressBar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
