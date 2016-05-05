package com.example.jaylen.popularmovies;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MoviesFragment extends Fragment {
    GridView moviesView ;
    MoviesAdapter moviesAdapter;
    ArrayList<HashMap<String,Object>> moviesItem = new ArrayList<HashMap<String,Object>>();
    ArrayList<MovieInfo> movieInfos = new ArrayList<MovieInfo>();
    NetworkChangeReceiver receiver ;
    int flag = 0;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview= inflater.inflate(R.layout.fragment_main,container,false);
        moviesView = (GridView)rootview.findViewById(R.id.movies_grid);
        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("POSTER",movieInfos.get(position).poster_path);
                intent.putExtra("TITLE",movieInfos.get(position).title);
                intent.putExtra("O_TITLE",movieInfos.get(position).original_title);
                intent.putExtra("DATE",movieInfos.get(position).release_date);
                intent.putExtra("OVERVIEW",movieInfos.get(position).overview);
                intent.putExtra("RATE",movieInfos.get(position).vote_average);
                flag = 0;
                startActivity(intent);
            }
        });
        return rootview;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<MovieInfo>>{
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w342/";
        @Override
        protected ArrayList<MovieInfo> doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try{
                final String MOVIESDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?";
                final String QUERY_PARAM = "sort_by";
                final String API_KEY = "api_key";
                Uri builtUri = Uri.parse(MOVIESDB_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,getString(R.string.api_key))
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line=reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return  null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e){
                Log.e(LOG_TAG,e.getMessage());
                //Toast.makeText(getContext(),"We Cannot Connect to the Internet,Please Open The Internet Connection and Try Again",Toast.LENGTH_SHORT).show();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.readLine();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,e.getMessage());
                    }
                }
            }

            try {
                if(moviesJsonStr != null){
                    return getMoviesDataFromJson(moviesJsonStr);
                }else {
                    return null;
                }
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieInfo> strings) {
            moviesAdapter = new MoviesAdapter(getActivity(),strings);
            moviesView.setAdapter(moviesAdapter);
            super.onPostExecute(strings);
        }

        private ArrayList<MovieInfo> getMoviesDataFromJson(String moviesJsonData)
        throws JSONException{
            movieInfos.clear();
            ArrayList<MovieInfo> resultList = new ArrayList<MovieInfo>();
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String ORIGINAL_TITLE = "original_title";
            final String ORIGINAL_LANGUAGE = "original_language";
            final String TITLE = "title";
            final String POPULARITY = "popularity";
            final String VOTE_AVERAGE = "vote_average";
            JSONObject moviesJson = new JSONObject(moviesJsonData);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
            for(int i = 0;i<moviesArray.length();i++){
                MovieInfo movieInfo = new MovieInfo();
                JSONObject jsonObject = moviesArray.getJSONObject(i);
                movieInfo.poster_path = BASE_POSTER_URL + jsonObject.getString(POSTER_PATH);
                movieInfo.overview = jsonObject.getString(OVERVIEW);
                movieInfo.release_date = jsonObject.getString(RELEASE_DATE);
                movieInfo.original_language = jsonObject.getString(ORIGINAL_LANGUAGE);
                movieInfo.title = jsonObject.getString(TITLE);
                movieInfo.popularity = jsonObject.getString(POPULARITY);
                movieInfo.vote_average = jsonObject.getString(VOTE_AVERAGE);
                movieInfo.original_title = jsonObject.getString(ORIGINAL_TITLE);
                resultList.add(movieInfo);
                movieInfos.add(movieInfo);
            }
            return resultList;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(flag == 1){
            UpdateMoviesData();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            UpdateMoviesData();
            receiver = new NetworkChangeReceiver();
            setHasOptionsMenu(true);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            getActivity().registerReceiver(receiver,filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            getActivity().unregisterReceiver(receiver);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            UpdateMoviesData();
            return true;
        }else if(id == R.id.action_setting){
            flag = 1;
            startActivity(new Intent(getActivity(),SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateMoviesData(){
        ConnectivityManager manager = (ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()){
            FetchMoviesTask task = new FetchMoviesTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_order = prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_pop_value));
            task.execute(sort_order);
        }else {
            Toast.makeText(getActivity(),"Network is disconnected",Toast.LENGTH_SHORT).show();
        }


    }

    class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo != null  && networkInfo.isAvailable()){
                Toast.makeText(getActivity(),"Network is connected",Toast.LENGTH_SHORT).show();
                UpdateMoviesData();
            }else {
                Toast.makeText(getActivity(),"Network is disconnected",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
