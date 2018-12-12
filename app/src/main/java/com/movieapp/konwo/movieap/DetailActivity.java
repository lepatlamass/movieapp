package com.movieapp.konwo.movieap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.movieapp.konwo.movieap.adapter.ReviewAdapter;
import com.movieapp.konwo.movieap.adapter.TrailerAdapter;
import com.movieapp.konwo.movieap.api.Client;
import com.movieapp.konwo.movieap.api.Service;
import com.movieapp.konwo.movieap.data.MovieDatabase;
import com.movieapp.konwo.movieap.model.Movie;
import com.movieapp.konwo.movieap.model.Review;
import com.movieapp.konwo.movieap.model.ReviewResults;
import com.movieapp.konwo.movieap.model.Trailer;
import com.movieapp.konwo.movieap.model.TrailerResponse;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    // movie key from Parcelable value
    private static final String MOVIE_KEY = "movie_key";
    private static final String TAG = DetailActivity.class.getSimpleName();

    TextView movieOfName, plotSynopsis, userRating, releaseDate;
    ImageView imageView;

    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private Movie movie;
    private MovieDatabase movieDb;
    private Executor executor;
    private boolean isFavorite;

    int movie_id;

    private final AppCompatActivity activity = DetailActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        initCollapsingToolbar();

        movieDb = MovieDatabase.getDatabase(this);
        executor = new MovieExec();

        imageView = findViewById(R.id.thumbnail_image_header);
        movieOfName = findViewById(R.id.title);
        plotSynopsis = findViewById(R.id.overviewText);
        userRating = findViewById(R.id.userText);
        releaseDate = findViewById(R.id.releaseDate);

        // get movie parcelable from intent
        Intent intentThatStartedThisActivity = getIntent();
        movie = intentThatStartedThisActivity.getParcelableExtra(MOVIE_KEY);

        movieOfName.setText(movie.getOriginalTitle());
        plotSynopsis.setText(movie.getOverview());

        String movieName = movie.getOriginalTitle();

        Double voteCount = movie.getVoteAverage();
        userRating.setText(String.valueOf(voteCount));
        releaseDate.setText(movie.getReleaseDate());

        Glide.with(this).load(movie.getPosterpath()).into(imageView);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(movieName);

        final MaterialFavoriteButton materialFavoriteButton = findViewById(R.id.favorite_button);
        materialFavoriteButton.setFavorite(false);

        // check if movie is already in favorites
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Movie movie1 = movieDb.movieDAO().getMovieById(movie.getId());
                // if movi1 is null, movie is'nt in favorite
                if (movie1 == null) {
                    // set the button state to false
                    materialFavoriteButton.setFavorite(false);
                } else {
                    // movie is already in favorite, set the button state to true
                    materialFavoriteButton.setFavorite(true);
                }
            }
        });

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        // if movie is favorite
                        if (favorite) {
                            SharedPreferences.Editor editor =
                                    getSharedPreferences(".DetailActivity", MODE_PRIVATE).edit();
                            saveFavorite();
                            editor.putBoolean("Added to favorites", true);
                            editor.apply();
                            Snackbar.make(buttonView, "added to favorite", Snackbar.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor =
                                    getSharedPreferences(".DetailActivity", MODE_PRIVATE).edit();
                            removeFavorite();
                            editor.putBoolean("Removed from favorites", true);
                            editor.apply();
                            Snackbar.make(buttonView, "removed from Favorite", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initViews();
    }

//    private void initCollapsingToolbar() {
//        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setTitle(" ");
//        AppBarLayout appBarLayout = findViewById(R.id.appbar);
//        appBarLayout.setExpanded(true);
//
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle(getString(R.string.movie_detail));
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbarLayout.setTitle(" ");
//                    isShow = false;
//                }
//            }
//        });
//    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view1);
        // call to populateTrailers()
        populateTrailers();
        Review();
    }

    private void populateTrailers() {
        // set the LayoutManager
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new TrailerAdapter(this);
        recyclerView.setAdapter(adapter);

        // load movies
        Service service = Client.getClient().create(Service.class);
        Call<TrailerResponse> call =
                service.getMovieTrailer(movie.getId(), BuildConfig.THE_MOVIE_DB_API_TOKEN);
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

    private void Review(){
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please get your API Key", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Client client = new Client();
                Service apiService = Client.getClient().create(Service.class);
                Call<Review> call = apiService.getReview(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);

                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null) {
                                List<ReviewResults> reviewResults = response.body().getResults();
                                MultiSnapRecyclerView recyclerView2 = findViewById(R.id.review_recyclerview);
                                LinearLayoutManager firstManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView2.setLayoutManager(firstManager);
                                recyclerView2.setAdapter(new ReviewAdapter(getApplicationContext(), reviewResults));
                                recyclerView2.smoothScrollToPosition(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, "unable to fetch data", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveFavorite () {
        // add a selected movie to favorite
        executor.execute(new Runnable() { // running in separate thread
            @Override
            public void run() {
                movieDb.movieDAO().insert(movie);
            }
        });
    }

    private void removeFavorite() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                movieDb.movieDAO().delete(movie);
            }
        });
    }

}
