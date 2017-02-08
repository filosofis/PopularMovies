package com.example.oscar.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.oscar.popularmovies.Data.MovieContract;
import com.example.oscar.popularmovies.Network.FetchReviewsTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    public ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviewList;
    private Movie movie;

    public DetailsActivityFragment() {
        System.out.println("details constructor");
        reviewList = new ArrayList<>();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateReviews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("DetailsFragment created...");

        Intent intent = getActivity().getIntent();
        movie = (Movie)intent.getSerializableExtra("movie");

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        //Intent intent = getActivity().getIntent();
        //System.out.println(intent.getSerializableExtra("movie").toString());
        ImageButton button = (ImageButton) rootView.findViewById(R.id.buttonFavorite);
        //final Movie movie = (Movie)intent.getSerializableExtra("movie");
        TextView title = (TextView) rootView.findViewById(R.id.textViewTitle);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.textViewReleaseDate);
        TextView voteAverage = (TextView) rootView.findViewById(R.id.textViewVoteAverage);
        TextView overView = (TextView) rootView.findViewById(R.id.textViewOverView);
        ImageView imageThumb = (ImageView) rootView.findViewById(R.id.imageViewThumb);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                System.out.println("Marked as Favorite");
                insertFavorite(movie);
            }
        });
        title.setText(movie.getOriginalTitle());
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(movie.getVoteAverage());
        overView.setText(movie.getOverView());

        reviewAdapter = new ReviewAdapter(getActivity(), reviewList);
        ListView listView = (ListView) rootView.findViewById(R.id.reviews);
        listView.setAdapter(reviewAdapter);

        Glide.with(getActivity()).load(movie.getThumbPath())
                .error(R.drawable.thumb)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageThumb);
        return rootView;
    }

    private void updateReviews(){
        System.out.println("update reviews");
        FetchReviewsTask reviewsTask = new FetchReviewsTask(getActivity(), reviewAdapter);
        reviewsTask.execute(movie.getMovieId());
    }

    public void insertFavorite(Movie movie){
        System.out.println("Attempting to insert " + movie.getTitle());
        System.out.println(movie.toString());
        Cursor cr = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_TITLE + " = ?",
                new String[]{movie.getTitle()},
                null
        );
        if(cr.moveToFirst()){ //Only true if the movie was already in the db
            System.out.println("Movie already a favorite");
        }else{
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER_THUMB, movie.getThumbPath());
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverView());
            cv.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
            cv.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());

            getContext().getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI, cv);
        }
        cr.close();
    }
}
