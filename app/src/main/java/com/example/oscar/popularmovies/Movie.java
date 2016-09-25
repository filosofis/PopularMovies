package com.example.oscar.popularmovies;

/**
 * Created by Oscar on 2016-09-20.
 */
public class Movie {
    String title;
    String posterPath;
    String overView;
    String releaseDate;
    String originalTitle;

    public Movie(String title, String posterPath, String overView, String releaseDate, String originalTitle) {
        this.title = title;
        this.posterPath = posterPath;
        this.overView = overView;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
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
}

