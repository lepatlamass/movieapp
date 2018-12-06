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

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.movieapp.konwo.movieap.adapter.TrailerAdapter;
import com.movieapp.konwo.movieap.api.Client;
import com.movieapp.konwo.movieap.api.Service;
import com.movieapp.konwo.movieap.data.MovieDatabase;
import com.movieapp.konwo.movieap.model.Movie;
import com.movieapp.konwo.movieap.model.Trailer;
import com.movieapp.konwo.movieap.model.TrailerResponse;

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
    
    private final AppCompatActivity activity = DetailActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

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

        Double voteCount = movie.getVoteAverage();
        userRating.setText(String.valueOf(voteCount));
        releaseDate.setText(movie.getReleaseDate());

        Glide.with(this).load(movie.getPosterpath()).into(imageView);

        MaterialFavoriteButton materialFavoriteButton = findViewById(R.id.favorite_button);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            SharedPreferences.Editor editor = getSharedPreferences(".DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Added", true);
                            saveFavorite();
                            editor.commit();
                            Snackbar.make(buttonView, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences(".DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Removed", true);
                            removeFavorite();
                            editor.commit();
                            Snackbar.make(buttonView, "Removed from Favorite", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );

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
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_detail));
                    isShow = true;
                } else if (isShow) {
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

    public void saveFavorite () {
        // add a selected movie to favorite
        executor.execute(new Runnable() { // running in separate thread
            @Override
            public void run() {
                movieDb.movieDAO().insert(movie);
            }
        });
        
        /**
        
        favoriteDbHelper = new FavoriteDbHelper(activity);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");


        favorite.setId(movie_id);
        favorite.setOriginalTitle(movieOfName.getText().toString().trim());
        favorite.setPosterpath(poster);
        favorite.setVoteAverage(Double.parseDouble(rate));
        favorite.setOverview(plotSynopsis.getText().toString().trim());

        favoriteDbHelper.addFavorite(favorite); **/
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
