package com.movieapp.konwo.movieap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.movieapp.konwo.movieap.model.Movie;


public class DetailActivity extends AppCompatActivity{
    TextView movieOfName, plotSynopsis, userRating, releaseDate;
    ImageView imageView;

    Movie movie = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        imageView = findViewById(R.id.thumbnail_image_header);
        movieOfName =  findViewById(R.id.title);
        plotSynopsis =  findViewById(R.id.overviewText);
        userRating =  findViewById(R.id.userText);
        releaseDate = findViewById(R.id.releaseDate);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("original_title")){


            movieOfName.setText(getIntent().getExtras().getString("original_title"));
            plotSynopsis.setText(getIntent().getExtras().getString("overview"));

            Double voteCount = getIntent().getExtras().getDouble("vote_average");
            userRating.setText(String.valueOf(voteCount));
            releaseDate.setText(getIntent().getExtras().getString("release_date"));



            Glide.with(this).load(getIntent().getExtras().getString("poster_path")).into(imageView);


        } else {
            Toast.makeText(this, "NO API DATA", Toast.LENGTH_SHORT).show();
        }
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_detail));
                    isShow=true;
                } else if (isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
