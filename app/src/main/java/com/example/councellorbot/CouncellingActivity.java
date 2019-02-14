package com.example.councellorbot;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouncellingActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private static final String API_URL = Config.ROOT_URL+"Api.php?apicall=councellingcollege";

    private CollegeAdapter collegeAdapter;
    private List<College> colleges;
    private ListView listViewNames;
    String field,marks,user_id;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_councelling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // get user id and password
        marks = user.get(UserSessionManager.KEY_TEST);
        field = user.get(UserSessionManager.KEY_FEILD);
        user_id = user.get(UserSessionManager.KEY_USER_ID);

        colleges = new ArrayList<College>();
        listViewNames = findViewById(R.id.listViewNames);

        getData();
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
                                        College college = new College(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("description"),
                                                jsonObject.getString("email"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("contact"),
                                                jsonObject.getString("image")
                                        );
                                        colleges.add(college);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                collegeAdapter = new CollegeAdapter(CouncellingActivity.this,R.layout.master,colleges);
                                listViewNames.setAdapter(collegeAdapter);
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
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("marks", marks);
                params.put("field", field);
                params.put("user_id", user_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
