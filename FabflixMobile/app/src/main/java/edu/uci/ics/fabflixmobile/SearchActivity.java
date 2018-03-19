package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private EditText query;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        query = (EditText)findViewById(R.id.search_box);

        if (savedInstanceState != null) {
            query.setText(savedInstanceState.getString("QUERY"));
        }
        preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().clear().commit();
        Log.d("Search", "onCreate");
    }

    // This callback is called only when there is a saved instance previously saved using
    // onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
    // other state here, possibly usable after onStart() has completed.
    // The savedInstanceState Bundle is same as the one used in onCreate().
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        query.setText(savedInstanceState.getString("QUERY"));
        Log.d("Search", "onRestoreInstanceState");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("QUERY", query.getText().toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
        Log.d("Search", "onSaveInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        query.setText(preferences.getString("QUERY", ""));
        Log.d("Search", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
        editor.putString("QUERY", query.getText().toString()); // value to store
        // Commit to storage
        editor.commit();
        Log.d("Search", "onPause");
    }

    public void goToMovieList(View view) {
        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        //get edit text input
        movie_query = query.getText().toString();

        if (movie_query.equals("")) {
            Toast.makeText(getApplicationContext(), "Input cannot be empty!", Toast.LENGTH_LONG).show();
        }
        else {

            String url = "http://54.183.216.2:8080/project4/Search?query=" + movie_query;

            Log.d("url", url);
            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //Log.d("response", response);
                            //((TextView)findViewById(R.id.http_response)).setText(response);
                            try {
                                JSONArray jsArray = new JSONArray(response);
                                if (jsArray.length() == 0) {
                                    Toast.makeText(getApplicationContext(), "No movie found!", Toast.LENGTH_LONG).show();
                                } else {
                                    //Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                                    Intent goToIntent = new Intent(SearchActivity.this, MovieListActivity.class);
                                    String[] movies = new String[jsArray.length()];
                                    for (int i = 0; i < jsArray.length(); i++) {
                                        JSONObject jsObject = jsArray.getJSONObject(i);
                                        movies[i] = jsObject.toString();
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArray("jsonArray", movies);
                                    goToIntent.putExtras(bundle);
                                    startActivity(goToIntent);
                                }
                            } catch (JSONException e) {

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("security.error", error.toString());
                        }
                    }
            );
            queue.add(postRequest);
        }

        return ;
    }

}
