package com.example.councellorbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    TextView bytes,sup,lin;
    EditText usr,pswd;
    ProgressDialog progressDialog;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new UserSessionManager(login.this);

        usr = (EditText) findViewById(R.id.usrusr);
        pswd = (EditText)findViewById(R.id.passwrd);

        lin = (TextView)findViewById(R.id.logiin);
        sup = (TextView)findViewById(R.id.sup);

        bytes = (TextView)findViewById(R.id.bytes);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf");
        bytes.setTypeface(custom_font);
        pswd.setTypeface(custom_font);
        sup.setTypeface(custom_font);
        lin.setTypeface(custom_font);
        usr.setTypeface(custom_font);

        sup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(login.this,signup.class);
                startActivity(it);
            }
        });

        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        usr.setError(null);
        pswd.setError(null);

        // Store values at the time of the login attempt.
        final String username = usr.getText().toString();
        final String password = pswd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            pswd.setError(getString(R.string.error_invalid_password));
            focusView = pswd;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            usr.setError(getString(R.string.error_field_required));
            focusView = usr;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            progressDialog = new ProgressDialog(login.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            GetUserDetailsFromServer(username,password);
                        }
                    }, 3000);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }

    /*
     * this method is saving the name to ther server
     *
     */
    private void GetUserDetailsFromServer(final String user, final String pass) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getString("error").equalsIgnoreCase("false")) {

                                    JSONObject userJson = obj.getJSONObject("user");

                                    String user_email = userJson.getString("email");
                                    String user_pass = userJson.getString("password");
                                    String id = userJson.getString("id");
                                    String username = userJson.getString("username");

                                    String contact = userJson.getString("contact");
                                    String name = userJson.getString("name");
                                    String address = userJson.getString("address");

                                    session.createUserLoginSession(user_email, id, user_pass, username,name,contact,address);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),obj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"No Record Exist", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"No Record Exist", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", user);
                    params.put("password", pass);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
