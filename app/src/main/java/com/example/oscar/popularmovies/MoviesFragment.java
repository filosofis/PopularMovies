package com.example.oscar.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;


public class MoviesFragment extends Fragment {
    private posterAdapter posterAdapter;
    private Context mContext;

    Movie[] movieList = {
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg"),
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg"),
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg"),
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg"),
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg"),
            new Movie("Interstellar",
                    "http://image.tmdb.org/t/p/w185//5N20rQURev5CNDcMjHVUZhpoCNC.jpg")
    };

    public MoviesFragment() {
        // Required empty public constructo
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        //setHasOptionsMenu(true);
    }
    @Override
    public void onStart(){
        super.onStart();
        //updateMovies();
    }

    private void updateMovies(){
        System.out.println("Updating movies.....");
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        posterAdapter = new posterAdapter(getActivity(), Arrays.asList(movieList));
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        gridView.setAdapter(posterAdapter);
        return rootView;
    }
    public class FetchMovieTask extends AsyncTask<String, Void,  Movie[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        Movie[] movieArr={null,null};
        String[] moviePosters=null;
        String[] movieTitles=null;


        private Movie[] getMoviesFromJson(String movieJSONStr)throws JSONException{
            System.out.println("Geting movies from Json");
            final String MOVIE_BASE_URL = " http://image.tmdb.org/t/p/";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            String movieName;
            String urlPart;
            JSONObject movieJson = new JSONObject(movieJSONStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            try
            {
                for (int i = 0; i < movieJson.length(); i++) {


                    // Get the JSON object representing the movie
                    JSONObject movie = movieArray.getJSONObject(i);

                    // Movie Name
                    movieName = movie.getString(TITLE);

                    urlPart= movie.getString(POSTER_PATH);

                    movieTitles[i]=movieName;
                    moviePosters[i] = MOVIE_BASE_URL + urlPart;
                    movieArr[i]=new Movie(movieTitles[i],moviePosters[i]);


                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return movieArr;
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
                final String QUERY_PARAM = "sort_by";
                //final String FORMAT_PARAM = "mode";
                //final String UNITS_PARAM = "units";
                //final String DAYS_PARAM = "cnt";

                //GO SEE SUNSHINE EXPLANATION BY PARAMS[0]??
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(ampersand).appendQueryParameter(QUERY_PARAM, sort_by)
                        .build();

                URL url = new URL(FORECAST_BASE_URL);

                System.out.println("Build URL " + FORECAST_BASE_URL);

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

    }
}