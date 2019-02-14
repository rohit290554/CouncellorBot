package com.example.councellorbot;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CollegeDetailActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private static final String API_URL = Config.ROOT_URL+"Api.php?apicall=showcollegebyid";
    String college_id;
    ImageView image;
    TextView name,email,contact,description,address;
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        college_id = getIntent().getStringExtra("college_id");
        //collapsingToolbarLayout= findViewById(R.id.toolbar_layout);
        //collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.councellor));


        image = findViewById(R.id.image);
        name = findViewById(R.id.college_name);
        email = findViewById(R.id.college_email);
        address = findViewById(R.id.college_address);
        description = findViewById(R.id.college_description);
        contact = findViewById(R.id.college_contact);

        getData();

        toolbar.setTitle(name.getText().toString());
    }

    public void getData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("TAG", response.toString());
                        // Parsing json
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("error").equalsIgnoreCase("false")) {
                                JSONArray jsonArray = obj.getJSONArray("college");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        Glide.with(CollegeDetailActivity.this)
                                                .asBitmap()
                                                .load(jsonObject.getString("url"))
                                                .into(image);

                                        name.setText(jsonObject.getString("name"));
                                        email.setText(jsonObject.getString("email"));
                                        contact.setText(jsonObject.getString("contact"));
                                        address.setText(jsonObject.getString("address"));
                                        description.setText(jsonObject.getString("description"));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        //on error storing the details to sqlite with status unsynced
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("college_id", college_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
