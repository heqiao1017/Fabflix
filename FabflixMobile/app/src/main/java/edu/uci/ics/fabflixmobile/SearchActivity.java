package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends ActionBarActivity {
    private String movie_query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void goToMovieList(View view) {
        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        //get edit text input
        movie_query = ((EditText)findViewById(R.id.search_box)).getText().toString();

        String url = "http://54.153.26.182:8080/project4/Search?query="+movie_query;

        Log.d("url", url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        //Log.d("response", response);
                        //((TextView)findViewById(R.id.http_response)).setText(response);
                        try {
                            JSONArray jsArray = new JSONArray(response);
                            if (jsArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No movie found!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                //Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                                Intent goToIntent = new Intent(SearchActivity.this, MovieListActivity.class);
                                String[] movies = new String[jsArray.length()];
                                for (int i = 0; i < jsArray.length(); i++) {
                                    JSONObject jsObject = jsArray.getJSONObject(i);
                                    movies[i] = jsObject.toString();
                                }
                                Bundle bundle=new Bundle();
                                bundle.putStringArray("jsonArray", movies);
                                goToIntent.putExtras(bundle);
                                startActivity(goToIntent);
                            }
                        }
                        catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        );
        queue.add(postRequest);
        return ;
    }

}
