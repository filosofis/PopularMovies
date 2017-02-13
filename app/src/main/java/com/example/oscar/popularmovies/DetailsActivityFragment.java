package com.example.oscar.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.oscar.popularmovies.Data.MovieContract;
import com.example.oscar.popularmovies.Network.FetchReviewsTask;
import com.example.oscar.popularmovies.Network.FetchTrailersTask;

import java.util.ArrayList;

/**
 * Fragment containing a detailed information about a movie
 */
public class DetailsActivityFragment extends Fragment {
    public ReviewAdapter reviewAdapter;
    public TrailerAdapter trailerAdapter;
    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;
    private Movie movie;

    public DetailsActivityFragment() {
        System.out.println("details constructor");
        reviewList = new ArrayList<>();
        trailerList = new ArrayList<>();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateReviews();
        updateTrailers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("DetailsFragment created...");

        Intent intent = getActivity().getIntent();
        movie = (Movie)intent.getSerializableExtra("movie");

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ImageButton button = (ImageButton) rootView.findViewById(R.id.buttonFavorite);
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
        String rating = movie.getVoteAverage() + "/10";
        voteAverage.setText(rating);
        overView.setText(movie.getOverView());

        //review stuff
        reviewAdapter = new ReviewAdapter(getActivity(), reviewList);
        ListView listView = (ListView) rootView.findViewById(R.id.reviews);
        listView.setAdapter(reviewAdapter);

        //Trailer stuff
        trailerAdapter = new TrailerAdapter(getActivity(), trailerList);
        ListView listViewTrailer = (ListView) rootView.findViewById(R.id.trailers);
        listViewTrailer.setAdapter(trailerAdapter);
        listViewTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                System.out.println(trailerAdapter.getItem(position).getKey());
                watchYoutubeVideo(trailerAdapter.getItem(position).getKey());
            }
        });

        Glide.with(getActivity()).load(movie.getThumbPath())
                .error(R.drawable.thumb)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageThumb);
        return rootView;
    }

    /**
     * Tries to open a youtube video in the youtube app, and defaults
     * to browser if the app is not available as suggested by
     * Roger Garzon Nieto on Stackoverflow
     */
    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    /**
     * Updates the trailer adapter using the FetchTrailersTask
     */
    private void updateTrailers(){
        System.out.println("update trailers");
        FetchTrailersTask trailersTask = new FetchTrailersTask(trailerAdapter);
        trailersTask.execute(movie.getMovieId());
    }

    /**
     * Updates the reviews adapter using FetchReviewTask
     */
    private void updateReviews(){
        System.out.println("update reviews");
        FetchReviewsTask reviewsTask = new FetchReviewsTask(reviewAdapter);
        reviewsTask.execute(movie.getMovieId());
    }

    /**
     * Inserts movie into a local databse of favoirte movies as long
     * as its not already in the database
     * @param movie
     */
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
