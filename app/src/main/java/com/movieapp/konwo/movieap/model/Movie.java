package com.movieapp.konwo.movieap.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    @SerializedName("id")
    private Integer id;
    @SerializedName("poster_path")
    private String posterpath;
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("genre_ids")
    private List<Integer> genreIds = new ArrayList<Integer>();
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backDropPath;
    @SerializedName("vote_count")
    private Integer voteCount;
    @SerializedName("video")
    private boolean video;
    @SerializedName("vote_average")
    private Double voteAverage;

    //constructor for movie
    public Movie(Integer id, String posterpath, boolean adult, String overview,
                 String releaseDate, List<Integer> genreIds, String originalTitle,
                 String originalLanguage, String title, String backDropPath, Integer voteCount,
                 boolean video, Double voteAverage) {
        this.id = id;
        this.posterpath = posterpath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.backDropPath = backDropPath;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
    }

    String imageBaseUrl = "https://image.tmdb.org/t/p/w500";

    public String getPosterpath() {
        return "https://image.tmdb.org/t/p/w500" + posterpath;
    }

    //generated getter
    public Integer getId() {
        return id;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    //generated setter
    public void setId(Integer id) {
        this.id = id;
    }

    public void setPosterpath(String posterpath) {
        this.posterpath = posterpath;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
