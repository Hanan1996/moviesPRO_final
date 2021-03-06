package com.example.amjad.moviespro;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Popular extends Activity {

    ArrayList<Movies> movieList;
    String url="http://api.themoviedb.org/3/movie/popular?api_key=591cbbe8228f336bee2d04c457683097";
    MoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieList = new ArrayList<Movies>();
        new JSONAsyncTask().execute(url);

        ListView listview = (ListView)findViewById(R.id.list);
        adapter = new MoviesAdapter(getApplicationContext(), R.layout.row, movieList);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), movieList.get(position).getTitle(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Popular.this,SingleItemView.class);
                intent.putExtra("title",movieList.get(position).getTitle());
                intent.putExtra("id",movieList.get(position).getId());
                intent.putExtra("vote_average",movieList.get(position).getVote_average());
                intent.putExtra("m_image",movieList.get(position).get_M_image());
                intent.putExtra("overview",movieList.get(position).getOverview());
                intent.putExtra("release_date",movieList.get(position).getRelease_date());
               // intent.putExtra("genre_ids",movieList.get(position).getGenrs());
                startActivity(intent);
            }
        });
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Popular.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("results");


                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Movies movie = new Movies();

                        movie.setTitle(object.getString("title"));
                        movie.setId(object.getString("id"));
                        movie.setVote_average(object.getString("vote_average"));
                        movie.setM_image(object.getString("poster_path"));
                        movie.setOverview(object.getString("overview"));
                        movie.setRelease_date(object.getString("release_date"));

                        movieList.add(movie);
                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }






}
