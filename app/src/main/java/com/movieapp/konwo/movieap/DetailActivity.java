package com.movieapp.konwo.movieap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.movieapp.konwo.movieap.adapter.TrailerAdapter;
import com.movieapp.konwo.movieap.api.Client;
import com.movieapp.konwo.movieap.api.Service;
import com.movieapp.konwo.movieap.model.Movie;
import com.movieapp.konwo.movieap.model.Trailer;
import com.movieapp.konwo.movieap.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity{

    // movie key from Parcelable value
    private static final String MOVIE_KEY = "movie_key";
    private static final String TAG = DetailActivity.class.getSimpleName();

    TextView movieOfName, plotSynopsis, userRating, releaseDate;
    ImageView imageView;

    private RecyclerView recyclerView;
    private TrailerAdapter adapter;

    private Movie movie;

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

        // get movie parcelable from intent
        Intent intentThatStartedThisActivity = getIntent();
        movie = intentThatStartedThisActivity.getParcelableExtra(MOVIE_KEY);

        if (intentThatStartedThisActivity.hasExtra("original_title")){

            movieOfName.setText(movie.getOriginalTitle());
            plotSynopsis.setText(movie.getOverview());

            Double voteCount = movie.getVoteAverage();
            userRating.setText(String.valueOf(voteCount));
            releaseDate.setText(movie.getReleaseDate());

            Glide.with(this).load(movie.getPosterpath()).into(imageView);


        } else {
            Toast.makeText(this, "NO API DATA", Toast.LENGTH_SHORT).show();
        }

        initViews();
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

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view1);
        // call to populateTrailers()
        populateTrailers();
    }

    private void populateTrailers() {
        // set the LayoutManager
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new TrailerAdapter(this);
        recyclerView.setAdapter(adapter);

        // load movies
        Service service = Client.getClient().create(Service.class);
        Call<TrailerResponse> call =
                service.getMovieTrailer(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful()) {
                    // TrailerResponse's results;
                    List<Trailer> trailers = response.body().results;
                    adapter.setItems(trailers);
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

}
