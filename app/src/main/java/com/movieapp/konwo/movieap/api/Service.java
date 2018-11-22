package com.movieapp.konwo.movieap.api;

import com.movieapp.konwo.movieap.model.MoviesResponse;
import com.movieapp.konwo.movieap.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apikey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apikey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("id") int id, @Query("api_key") String apikey);

}
