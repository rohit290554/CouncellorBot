package com.example.councellorbot;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCollegeActivity extends AppCompatActivity {
    MaterialSearchView searchView;
    ProgressDialog progressDialog;
    private static final String API_URL = Config.ROOT_URL+"Api.php?apicall=show";

    private CollegeAdapter collegeAdapter;
    private List<College> colleges,searchedColleges;
    private ListView listViewNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_college);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        colleges = new ArrayList<College>();
        searchedColleges = new ArrayList<College>();
        listViewNames = findViewById(R.id.listViewNames);
        getData();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchedColleges.clear();
                for (int i=0;i<colleges.size();i++) {
                    College name = colleges.get(i);
                    if(name.getName().toUpperCase().contains(newText.toUpperCase())) {
                        Log.e("College",name.getName().toUpperCase());
                        searchedColleges.add(name);
                    }
                }
                collegeAdapter = new CollegeAdapter(SearchCollegeActivity.this,R.layout.master,searchedColleges);
                listViewNames.setAdapter(collegeAdapter);

                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                collegeAdapter = new CollegeAdapter(SearchCollegeActivity.this,R.layout.master,colleges);
                listViewNames.setAdapter(collegeAdapter);

            }

            @Override
            public void onSearchViewClosed() {
                collegeAdapter = new CollegeAdapter(SearchCollegeActivity.this,R.layout.master,colleges);
                listViewNames.setAdapter(collegeAdapter);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
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
                                collegeAdapter = new CollegeAdapter(SearchCollegeActivity.this,R.layout.master,colleges);
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
                });
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
