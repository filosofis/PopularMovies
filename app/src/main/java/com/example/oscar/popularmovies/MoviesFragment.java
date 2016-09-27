package com.example.oscar.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MoviesFragment extends Fragment {
    public posterAdapter posterAdapter;
    private ArrayList<Movie> movieList;
    SharedPreferences prefs;
    public MoviesFragment() {
        // Required empty public constructo
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
                    getString(R.string.pref_sort_key),
                    "popularity.desc");
            editor.apply();
            updateMovies();
        }
        if(id == R.id.sort_rating){
            editor.putString(
                    getString(R.string.pref_sort_key),
                    "vote_average.dsc");
            editor.apply();
            updateMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }

    private void updateMovies(){
        System.out.println("Updating movies...");
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Creating View...");
        final View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        posterAdapter = new posterAdapter(getActivity(), movieList);
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
                /*System.out.println(
                        posterAdapter.getItem(position).getOriginalTitle()
                );*/
            }
        });
        return rootView;
    }
    public class FetchMovieTask extends AsyncTask<String, Void,  Movie[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie[] getMoviesFromJson(String movieJSONStr)throws JSONException{
            System.out.println("Geting movies from Json");
            final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/w185";
            final String MOVIE_THUMB_URL = "http://image.tmdb.org/t/p/w92";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            final String RELEASE_DATE = "release_date";
            final String OVERVIEW = "overview";
            final String ORIGINAL_TITLE = "original_title";
            final String VOTE_AVERAGE = "vote_average";
            String movieTitle;
            String urlPart;
            String overView;
            String releaseDate;
            String originalTitle;
            String voteAverage;
            JSONObject movieJson = new JSONObject(movieJSONStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            Movie[] movieResult = new Movie[movieArray.length()];
            System.out.println("Json length  " + movieArray.length());
            try
            {
                for (int i = 0; i <movieArray.length(); i++) {


                    // Get the JSON object representing the movie
                    JSONObject movie = movieArray.getJSONObject(i);


                    movieTitle = movie.getString(TITLE);
                    urlPart = movie.getString(POSTER_PATH);
                    releaseDate = movie.getString(RELEASE_DATE);
                    overView = movie.getString(OVERVIEW);
                    originalTitle = movie.getString(ORIGINAL_TITLE);
                    voteAverage = movie.getString(VOTE_AVERAGE);
                    movieResult[i] = new Movie(
                            movieTitle,
                            MOVIE_BASE_URL + urlPart,
                            MOVIE_THUMB_URL + urlPart,
                            overView,
                            releaseDate,
                            originalTitle,
                            voteAverage);

                    /*movieTitles[i]=movieName;
                    moviePosters[i] = MOVIE_BASE_URL + urlPart;
                    movieArray[i]=new Movie(movieTitles[i],moviePosters[i]);*/


                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return movieResult;
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            System.out.println("Doing stuff in background...");
            /*if (strings.length == 0) {
                System.out.println("No params");
                return null;
            }*/
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String sort_by="popularity.desc";
            String ampersand= "&";

            try {
                System.out.println("Trying");
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/popular?api_key=941b7f72dda3e30dee0803975fca2f05";
                final String BASE_URL="https://api.themoviedb.org/3/discover/movie?api_key=";
                final String SORT = "&sort_by=";
                String OPTION = prefs.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_default));
                final String API_KEY = BuildConfig.API_KEY;

                //GO SEE SUNSHINE EXPLANATION BY PARAMS[0]??
                /*Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(ampersand).appendQueryParameter(QUERY_PARAM, sort_by)
                        .build();*/

                URL url = new URL(BASE_URL + API_KEY + SORT + OPTION);

                System.out.println("Build URL " + url);

                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=84604ead3481bd3bbd687f383f87e738");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                /*if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }*/
                movieJsonStr = buffer.toString();

                System.out.println("Movie string: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

                try {
                    return getMoviesFromJson(movieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            int count=0;
            if(movies != null){
                posterAdapter.clear();
                for(Movie movie : movies){
                    posterAdapter.add(movie);
                    count++;
                }
            }
            System.out.println("Post execute added " + count + " movies");
        }
    }
}
