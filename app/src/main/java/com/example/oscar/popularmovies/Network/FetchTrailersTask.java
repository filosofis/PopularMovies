package com.example.oscar.popularmovies.Network;

import android.os.AsyncTask;
import android.util.Log;
import com.example.oscar.popularmovies.BuildConfig;
import com.example.oscar.popularmovies.Trailer;
import com.example.oscar.popularmovies.TrailerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches trailers for a given movie from TheMovieDB
 */

public class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {

    private TrailerAdapter trailerAdapter;

    public FetchTrailersTask(TrailerAdapter trailerAdapter){
        this.trailerAdapter = trailerAdapter;
    }

    private Trailer[] getTrailersFromJson(String trailerJSONstr) throws JSONException{

        final String RESULTS = "results";
        final String TITLE = "name";
        final String KEY = "key";
        String trailerTitle;
        String trailerKey;

        JSONObject trailerJson = new JSONObject(trailerJSONstr);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);
        Trailer[] trailerResult = new Trailer[trailerArray.length()];

        try{
            for(int i=0; i<trailerArray.length(); i++){
                JSONObject trailer = trailerArray.getJSONObject(i);
                trailerTitle = trailer.getString(TITLE);
                trailerKey = trailer.getString(KEY);

                trailerResult[i] = new Trailer(trailerTitle, trailerKey);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return trailerResult;
    }

    @Override
    protected Trailer[] doInBackground(String... params){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailerJsonStr = null;
        try{
            final String BASE_URL="http://api.themoviedb.org/3/movie/";
            final String API = "/videos?api_key=";
            final String API_KEY = BuildConfig.API_KEY;
            final String MOVIE_ID = params[0];

            URL url = new URL(BASE_URL + MOVIE_ID + API + API_KEY);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            trailerJsonStr = buffer.toString();
        }
        catch(IOException e){
            Log.e("", "Error ", e);
            return null;
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("", "Error closing stream", e);
                }
            }

            try {
                return getTrailersFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e("", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return new Trailer[0];
    }

    @Override
    protected void onPostExecute(Trailer[] trailers){
        int count=0;
        if(trailers != null){
            trailerAdapter.clear();
            for(Trailer trailer : trailers){
                trailerAdapter.add(trailer);
                count++;
            }
        }
        System.out.println("Post execute added " + count + " trailers");
    }
}
