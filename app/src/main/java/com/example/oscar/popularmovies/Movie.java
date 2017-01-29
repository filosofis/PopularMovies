package com.example.oscar.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Oscar on 2016-09-20.
 */
public class Movie implements Serializable {
    private String title;
    private String posterPath;
    private String thumbPath;
    private String overView;
    private String releaseDate;
    private String originalTitle;
    private String voteAverage;
    private String movieId;


    public Movie(String title,
                 String posterPath,
                 String thumbPath,
                 String overView,
                 String releaseDate,
                 String originalTitle,
                 String voteAverage,
                 String movieId) {
        this.title = title;
        this.posterPath = posterPath;
        this.thumbPath = thumbPath;
        this.overView = overView;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.voteAverage = voteAverage;
        this.movieId = movieId;

    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getMovieId() {return movieId;}

    public void setMovieId(String movieId) {this.movieId = movieId;}

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", thumbPath='" + thumbPath + '\'' +
                ", overView='" + overView + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                ", movieId='" + movieId + '\'' +
                '}';
    }
}

