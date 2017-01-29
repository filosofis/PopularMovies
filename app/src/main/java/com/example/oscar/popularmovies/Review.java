package com.example.oscar.popularmovies;

/**
 * Created by Oscar on 2017-01-29.
 */

public class Review {
    private String author;
    private String content;

    public Review(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}