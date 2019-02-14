package com.example.councellorbot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    private static final String API_URL = Config.ROOT_URL+"Api.php?apicall=showbyid";
    private static final String EDUCATION_URL = Config.ROOT_URL+"Api.php?apicall=educationrecords";
    private static final String DATA_URL = Config.ROOT_URL+"Api.php?apicall=record";
    String user_pass,user_id,user_name,role_id,feild_id;
    UserSessionManager session;
    ImageView imageView;
    private DrawerLayout mDrawerLayout;
    TextView usertitle;
    EditText highschool,intermediate,physics,chemistry,maths,feilds,marks;
    Button record;

    ProgressDialog progressDialog,loading,loadingDialog;

    static boolean cancel = false;
    View focusView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new UserSessionManager(getApplicationContext());
        // Check user login
        // If User is not logged in , This will redirect user to LoginActivity.
        if (session.checkLogin()) {
            finish();
            return;
        }
        HashMap<String, String> user = session.getUserDetails();
        // get user id and password
        user_name = user.get(UserSessionManager.KEY_USERNAME);
        user_id = user.get(UserSessionManager.KEY_USER_ID);
        user_pass = user.get(UserSessionManager.KEY_USER_PASS);
        role_id = user.get(UserSessionManager.KEY_ROLE_ID);

        highschool = findViewById(R.id.hs);
        intermediate = findViewById(R.id.im);
        physics = findViewById(R.id.phy);
        chemistry = findViewById(R.id.chem);
        maths = findViewById(R.id.maths);
        feilds = findViewById(R.id.fields);
        marks = findViewById(R.id.marks);

        record =findViewById(R.id.calculate);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View v = navigationView.getHeaderView(0);
        usertitle = v.findViewById(R.id.shopnm);
        imageView = v.findViewById(R.id.circleView);

        getData();
        getDetails();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu_black_24dp, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        if(menuItem.getTitle().toString().equalsIgnoreCase("Profile"))
                        {
                            Intent obj=new Intent(MainActivity.this,UpdateProfileActivity.class);
                            startActivity(obj);
                        }
                        else if(menuItem.getTitle().toString().equalsIgnoreCase("Change Password"))
                        {
                            showpasswordform();
                        }
                        else if(menuItem.getTitle().toString().equalsIgnoreCase("Search College"))
                        {
                            Intent obj=new Intent(MainActivity.this,SearchCollegeActivity.class);
                            startActivity(obj);
                        }
                        else if(menuItem.getTitle().toString().equalsIgnoreCase("Start Test"))
                        {
                            Intent obj=new Intent(MainActivity.this,StartTestActivity.class);
                            startActivity(obj);
                        }
                        else if(menuItem.getTitle().toString().equalsIgnoreCase("Logout"))
                        {
                            session.logoutUser();
                            finish();
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(record.getText().toString().equalsIgnoreCase("SAVE")) {
                    validateRecord();
                } else {
                    Intent intent = new Intent(MainActivity.this,CouncellingActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                session.logoutUser();
                finish();
                break;

            case android.R.id.home :
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                                JSONArray jsonArray = obj.getJSONArray("users");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if(!jsonObject.getString("username").equalsIgnoreCase("null")) {
                                            usertitle.setText(jsonObject.getString("username"));
                                        }
                                        if(!jsonObject.getString("url").equalsIgnoreCase("null")) {
                                            Glide.with(MainActivity.this)
                                                    .asBitmap()
                                                    .load(Config.IMAGE_URL + jsonObject.getString("url"))
                                                    .into(imageView);
                                        }
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
                params.put("user_id", user_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    public void getDetails() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Fetching Details...");
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EDUCATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismiss();
                        Log.d("TAG", response.toString());
                        // Parsing json
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("error").equalsIgnoreCase("false")) {
                                JSONArray jsonArray = obj.getJSONArray("records");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        highschool.setText(jsonObject.getString("highschool"));
                                        intermediate.setText(jsonObject.getString("intermediate"));
                                        physics.setText(jsonObject.getString("physics"));
                                        maths.setText(jsonObject.getString("maths"));
                                        chemistry.setText(jsonObject.getString("chemistry"));
                                        if(jsonObject.getString("marks").equalsIgnoreCase("NULL")) {
                                            record.setText("Record");
                                        } else {
                                            record.setText("Go For Councelling");
                                            marks.setText(jsonObject.getString("marks"));
                                            session.recordMarks(jsonObject.getString("marks"));
                                        }
                                        if(jsonObject.getString("field").equalsIgnoreCase("1")) {
                                            feilds.setText("Medical");
                                            feild_id = jsonObject.getString("field");
                                            session.recordField(feild_id);
                                        } else if(jsonObject.getString("field").equalsIgnoreCase("2")) {
                                            feilds.setText("Engineering");
                                            feild_id = jsonObject.getString("field");
                                            session.recordField(feild_id);
                                        } else if(jsonObject.getString("field").equalsIgnoreCase("3")) {
                                            feilds.setText("Law");
                                            feild_id = jsonObject.getString("field");
                                            session.recordField(feild_id);
                                        } else if(jsonObject.getString("field").equalsIgnoreCase("4")) {
                                            feilds.setText("10th");
                                            feild_id = jsonObject.getString("field");
                                            session.recordField(feild_id);
                                        } else {
                                            feilds.setText("");
                                        }

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
                        loadingDialog.dismiss();
                        error.printStackTrace();
                        //on error storing the details to sqlite with status unsynced
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void showpasswordform() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.password_dialog, (ViewGroup) findViewById(R.id.root),false);

        final EditText password1 = (EditText) layout.findViewById(R.id.password);
        final EditText password2 = (EditText) layout.findViewById(R.id.conf_password);
        final EditText password3 = (EditText) layout.findViewById(R.id.current_password);
        final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);
        final Button yes = (Button) layout.findViewById(R.id.ld_btn_yes);
        final Button no = (Button) layout.findViewById(R.id.ld_btn_negative);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);


        //Adding our dialog box to the view of alert dialog
        alert.setView(layout);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPass3 = password3.getText().toString();
                String strPass1 = password1.getText().toString();
                String strPass2 = password2.getText().toString();
                Log.i("Result",strPass1+" "+strPass2);
                password1.setError(null);
                password2.setError(null);
                password3.setError(null);
                if (!user_pass.equals(Config.hashPassword(strPass3))) {
                    password3.setError(getString(R.string.current_pwd_not_equal));
                    focusView = password3;
                    cancel = true;
                }
                else if(TextUtils.isEmpty(strPass1)) {
                    password1.setError(getString(R.string.error_field_required));
                    focusView = password1;
                    cancel = true;
                }
                else if(!isPasswordValid(strPass1)) {
                    password1.setError(getString(R.string.error_incorrect_password));
                    focusView = password1;
                    cancel = true;
                }
                else if(TextUtils.isEmpty(strPass2)) {
                    password2.setError(getString(R.string.error_field_required));
                    focusView = password2;
                    cancel = true;
                } else if(!strPass1.equals(strPass2)) {
                    password2.setError(getString(R.string.settings_pwd_not_equal));
                    focusView = password2;
                    cancel = true;
                }
                else {
                    cancel = false;
                }
                if(cancel)
                {
                    focusView.requestFocus();
                }
                else {
                    alertDialog.dismiss();
                    loading = new ProgressDialog(MainActivity.this);
                    loading.setIndeterminate(true);
                    loading.setMessage("Updating Password...");
                    loading.show();
                    updatePassword(password1.getText().toString());
                }
            }
        });


    }

    /*
     * this method is changing the current password
     * */
    private void updatePassword(final String pass) {
        //Displaying a progressbar
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ROOT_URL + "Api.php?apicall=changepassword",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            loading.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getString("error").equalsIgnoreCase("false")) {
                                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id",user_id);
                    params.put("user_pass", pass);

                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    /*
     * By passing theme as a second argument to constructor - we can tint checkboxes/edittexts.
     * Don't forget to set your theme's parent to Theme.AppCompat.Light.Dialog.Alert
     */
    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        if(password.length()>=3) {
            return true;
        }
        return false;
    }


    public void recordData(final String high, final String inter, final String phy, final String chem, final String math) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("TAG", response.toString());
                        // Parsing json
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("error").equalsIgnoreCase("false")) {
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Error in inserting record",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error in inserting record",Toast.LENGTH_LONG).show();
                        //on error storing the details to sqlite with status unsynced
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("highschool", high);
                params.put("intermediate", inter);
                params.put("physics", phy);
                params.put("chemistry", chem);
                params.put("maths", math);
                params.put("user_id", user_id);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void validateRecord() {

        // Reset errors.
        highschool.setError(null);
        intermediate.setError(null);
        physics.setError(null);
        chemistry.setError(null);
        maths.setError(null);

        // Store values at the time of the login attempt.
        final String high = highschool.getText().toString();
        final String inter = intermediate.getText().toString();

        final String phy = physics.getText().toString();
        final String chem = chemistry.getText().toString();
        final String math = maths.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(high)) {
            highschool.setError(getString(R.string.error_field_required));
            focusView = highschool;
            cancel = true;
        } else if (Double.parseDouble(high) > 100) {
            highschool.setError("Percentage should be less than 100");
            focusView = highschool;
            cancel = true;
        }

        if (TextUtils.isEmpty(inter)) {
            intermediate.setError(getString(R.string.error_field_required));
            focusView = intermediate;
            cancel = true;
        } else if (Double.parseDouble(inter) > 100) {
            intermediate.setError("Percentage should be less than 100");
            focusView = intermediate;
            cancel = true;
        }

        if (TextUtils.isEmpty(phy)) {
            physics.setError(getString(R.string.error_field_required));
            focusView = physics;
            cancel = true;
        } else if (Double.parseDouble(phy) > 100) {
            physics.setError("Marks should be less than 100");
            focusView = physics;
            cancel = true;
        }

        if (TextUtils.isEmpty(chem)) {
            chemistry.setError(getString(R.string.error_field_required));
            focusView = chemistry;
            cancel = true;
        } else if (Double.parseDouble(chem) > 100) {
            chemistry.setError("Marks should be less than 100");
            focusView = chemistry;
            cancel = true;
        }

        if (TextUtils.isEmpty(math)) {
            maths.setError(getString(R.string.error_field_required));
            focusView = maths;
            cancel = true;
        } else if (Double.parseDouble(math) > 100) {
            maths.setError("Marks should be less than 100");
            focusView = maths;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving Details...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            recordData(high,inter,phy,chem,math);
                        }
                    }, 3000);
        }
    }
}
