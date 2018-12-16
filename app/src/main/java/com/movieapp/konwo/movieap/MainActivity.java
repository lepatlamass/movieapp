package com.movieapp.konwo.movieap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.movieapp.konwo.movieap.adapter.MoviesAdapter;
import com.movieapp.konwo.movieap.api.Client;
import com.movieapp.konwo.movieap.api.Service;
import com.movieapp.konwo.movieap.data.MovieDatabase;
import com.movieapp.konwo.movieap.data.MovieViewModel;
import com.movieapp.konwo.movieap.model.Movie;
import com.movieapp.konwo.movieap.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    private static String LIST_STATE = "list_state";
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private ArrayList<Movie> moviesInstance = new ArrayList<>();

    private MovieDatabase movieDatabase;
    private MovieExec executor;
    private MovieViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            // get viewModel from ViewModelProviders class
            viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

            movieDatabase = MovieDatabase.getDatabase(this);
            executor = new MovieExec();
        } else {
            initViews();
        }

    }



    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
//        adapter = new MoviesAdapter(this, movieList);
        adapter = new MoviesAdapter(this, moviesInstance);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        restoreLayoutManagerPosition();
        adapter.notifyDataSetChanged();

        swipeContainer = findViewById(R.id.main_activity);
        swipeContainer.setColorSchemeColors(android.R.color.white);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        checkSortOder();

    }



    private void restoreLayoutManagerPosition() {
        if (savedRecyclerLayoutState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(LIST_STATE, moviesInstance);
        savedInstanceState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
        savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadFavoritesMovies(){
        recyclerView = findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getAllFavorite();
    }

    private void loadPopularMovies(){

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtaib API key fron themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            retrofit2.Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>(){

                @Override
                public void onResponse(retrofit2.Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResult();
                    moviesInstance.addAll(movies);
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }

//                @Override
//                public void onResponse(Call<MoviesResponse> call1, Response<MoviesResponse> responses) {
//
//                }
//
//                @Override
//                public void onFailure(Call<MoviesResponse> call,  Throwable t) {
//                    Log.d("Error", t.getMessage());
//                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
//                }
            });
        }catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTopRatedMovies(){

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtaib API key fron themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            retrofit2.Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>(){

                @Override
                public void onResponse(retrofit2.Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResult();
                    moviesInstance.addAll(movies);
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }

//                @Override
//                public void onResponse(Call<MoviesResponse> call1, Response<MoviesResponse> responses) {
//
//                }
//
//                @Override
//                public void onFailure(Call<MoviesResponse> call,  Throwable t) {
//                    Log.d("Error", t.getMessage());
//                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
//                }
            });
        }catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        Log.d(LOG_TAG, "Preferences updated");
//        checkSortOder();
//    }

    private void checkSortOder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.order_key),
                this.getString(R.string.most_pop)
        );
        if (sortOrder.equals(this.getString(R.string.most_pop))){
            Log.d(LOG_TAG,  "Sorting by most populsr");
            loadPopularMovies();
        } else if
                (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "Sorting by favorite");
            loadFavoritesMovies();
        }
        else {
            Log.d(LOG_TAG, "Sorting by vote average");
            loadTopRatedMovies();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieList.isEmpty()){
            checkSortOder();
        }
    }

    // load favorites by observing changes from the database
    @SuppressLint("StaticFieldLeak")
    private void getAllFavorite() {
        viewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
            }
        });
    }
}
