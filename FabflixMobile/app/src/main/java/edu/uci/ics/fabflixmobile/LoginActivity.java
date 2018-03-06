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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity{
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

//    // This callback is called only when there is a saved instance previously saved using
//    // onSaveInstanceState(). We restore some state in onCreate() while we can optionally restore
//    // other state here, possibly usable after onStart() has completed.
//    // The savedInstanceState Bundle is same as the one used in onCreate().
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        ((TextView)findViewById(R.id.email)).setText(savedInstanceState.getString("USER_NAME_KEY"));
//        ((TextView)findViewById(R.id.password)).setText(savedInstanceState.getString("USER_PASSWORD_KEY"));
//    }
//
//    // invoked when the activity may be temporarily destroyed, save the instance state here
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putString("USER_NAME_KEY", ((EditText)findViewById(R.id.email)).getText().toString());
//        outState.putString("USER_PASSWORD_KEY", ((EditText)findViewById(R.id.password)).getText().toString());
//
//        // call superclass to save any view hierarchy
//        super.onSaveInstanceState(outState);
//    }

    public void connectToTomcat(View view){

        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        //get edit text input
        email = ((EditText)findViewById(R.id.email)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();

        String url = "http://54.153.26.182:8080/project4/AndroidLogin?username="+email+"&password="+password;

        Log.d("url", url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);
                        //((TextView)findViewById(R.id.http_response)).setText(response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String status = jObject.getString("status");
                            String message = jObject.getString("message");

                            if (status.equals("success")) {
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                Intent goToIntent = new Intent(LoginActivity.this, SearchActivity.class);
                                startActivity(goToIntent);
                            }else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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


        // Add the request to the RequestQueue.
        queue.add(postRequest);


        return ;
    }
}
