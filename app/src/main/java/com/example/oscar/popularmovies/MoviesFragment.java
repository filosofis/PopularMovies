package com.example.oscar.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.oscar.popularmovies.Data.MovieContract;
import com.example.oscar.popularmovies.Network.FetchMovieTask;

import java.util.ArrayList;


public class MoviesFragment extends Fragment {
    public PosterAdapter posterAdapter;
    private ArrayList<Movie> movieList;
    SharedPreferences prefs;
    public MoviesFragment() {
        // Required empty public constructor
        System.out.println("Contructor constructing....");
        movieList = new ArrayList<>();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        System.out.println("onCreate...");
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        SharedPreferences.Editor editor = prefs.edit();
        if(id == R.id.sort_popular){
            editor.putString(
                    getString(R.string.endpoint_key),
                    "popular");
            editor.apply();
            updateMovies();
        }
        if(id == R.id.sort_rating){
            editor.putString(
                    getString(R.string.endpoint_key),
                    "top_rated");
            editor.apply();
            updateMovies();
        }
        if(id == R.id.sort_favorite){
            updateFavoriteMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }

    private void updateFavoriteMovies(){
        System.out.println("Updating favorite movies...");
        posterAdapter.clear();

        Cursor cr = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMNS,
                null,
                null,
                null
        );

        while(cr.moveToNext()){
            System.out.println("Building movie list...");
            posterAdapter.add(new Movie(
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)),
                    cr.getString(cr.getColumnIndex((MovieContract.MovieEntry.COLUMN_POSTER_THUMB))),
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                    cr.getString(cr.getColumnIndex((MovieContract.MovieEntry.COLUMN_DATE))),
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)),
                    cr.getString(cr.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))
            ));
        }
        cr.close();
    }
    private void updateMovies(){
        System.out.println("Updating movies...");
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), posterAdapter, prefs);
        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Creating View...");
        final View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        posterAdapter = new PosterAdapter(getActivity(), movieList);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        gridView.setAdapter(posterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                System.out.println("Putting extra: " +
                        posterAdapter.getItem(position).toString());
                intent.putExtra(
                        "movie",
                        posterAdapter.getItem(position)
                        );
                startActivity(intent);
            }
        });
        return rootView;
    }
}
