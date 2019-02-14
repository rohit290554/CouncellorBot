package com.example.councellorbot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.bumptech.glide.Glide;

public class UpdateProfileActivity extends AppCompatActivity {

    UserSessionManager session;
    EditText mUsernameView,mEmailView,mName,mAddress,mContact;
    RadioGroup mGender;
    RadioButton rb;
    ProgressDialog progressDialog;

    Button photo;
    ImageView image;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask,uploadImage,user_id;
    private Bitmap bitmap;

    SimpleDateFormat dateFormatter;
    public static final String TAG = UpdateProfileActivity.class.getSimpleName();

    private static final String URL = Config.ROOT_URL+"Api.php?apicall=showbyid";
    private static final String PROFILE_URL = Config.ROOT_URL+"Api.php?apicall=edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Profile");

        session = new UserSessionManager(UpdateProfileActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        // get user id
        user_id = user.get(UserSessionManager.KEY_USER_ID);

        // Set up the login form.
        mUsernameView = findViewById(R.id.your_user_name);
        mEmailView = findViewById(R.id.your_email_address);

        mName = findViewById(R.id.your_name);
        mAddress = findViewById(R.id.your_address);
        mGender = findViewById(R.id.gender);
        mContact = findViewById(R.id.your_contact);

        photo = findViewById(R.id.photo);
        image = findViewById(R.id.userpic);

        getData();


        Button update = findViewById(R.id.update_button);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        dateFormatter = new SimpleDateFormat(
                Config.DATE_FORMAT, Locale.ENGLISH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
                finish();
            super.onBackPressed();
            return;
        }
    }

    private void attemptSignup() {

        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);

        mName.setError(null);
        mContact.setError(null);
        mAddress.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String email = mEmailView.getText().toString();

        final String name = mName.getText().toString();
        final String address = mAddress.getText().toString();
        final String contact = mContact.getText().toString();

        int index = mGender.getCheckedRadioButtonId();
        rb = findViewById(index);
        final String gender = rb.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        /*if (TextUtils.isEmpty(address)) {
            mAddress.setError(getString(R.string.error_field_required));
            focusView = mAddress;
            cancel = true;
        }*/

        if (TextUtils.isEmpty(contact)) {
            mContact.setError(getString(R.string.error_field_required));
            focusView = mContact;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            registerUser(username,email,name,contact,gender,address);
                        }
                    }, 3000);
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void registerUser(final String user, final String email, final String name, final String contact, final String gender, final String address) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("error").equalsIgnoreCase("false")) {

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),obj.getString("message"), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error",error.toString());
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", user_id);
                params.put("username", user);
                params.put("email", email);

                params.put("gender", gender);
                params.put("name", name);
                params.put("address", address);
                params.put("contact", contact);

                params.put("image","img_" + dateFormatter.format(new Date()).toString() + ".png");
                params.put("pic",uploadImage);

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void selectImage() {
        boolean result=Utility.checkPermission(UpdateProfileActivity.this);
        if(result) {
            final CharSequence[] items = { "Take Photo", "Choose from Library",
                    "Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        userChoosenTask ="Take Photo";
                        cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask ="Choose from Library";
                        galleryIntent();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });


            builder.show();
        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
            uploadImage = getStringImage(bitmap);
            if (uploadImage.equals("") && uploadImage.equals(null)) {
                Toast.makeText(UpdateProfileActivity.this,"Wrong Input", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = thumbnail;
        image.setImageBitmap(thumbnail);
    }


    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //bm.compress(Bitmap.CompressFormatJPEG, 90, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmap = bm;
        image.setImageBitmap(bm);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public void getData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, response.toString());
                        // Parsing json
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("error").equalsIgnoreCase("false")) {
                                JSONArray jsonArray = obj.getJSONArray("users");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        if(!jsonObject.getString("name").equalsIgnoreCase("null")) {
                                            mName.setText(jsonObject.getString("name"));
                                        }
                                        mEmailView.setText(jsonObject.getString("email"));
                                        mUsernameView.setText(jsonObject.getString("username"));
                                        mContact.setText(jsonObject.getString("contact"));

                                        if(jsonObject.getString("gender").equalsIgnoreCase("male")) {
                                            RadioButton rb = findViewById(R.id.radioButton1);
                                            rb.setChecked(true);
                                        } else if(jsonObject.getString("gender").equalsIgnoreCase("female")) {
                                            RadioButton rb = findViewById(R.id.radioButton2);
                                            rb.setChecked(true);
                                        } else {
                                            RadioButton rb = findViewById(R.id.radioButton3);
                                            rb.setChecked(true);
                                        }

                                        if(!jsonObject.getString("address").equalsIgnoreCase("null")) {
                                            mAddress.setText(jsonObject.getString("address"));
                                        }
                                        if(!jsonObject.getString("url").equalsIgnoreCase("null")) {
                                            Glide.with(UpdateProfileActivity.this)
                                                    .asBitmap()
                                                    .load(Config.IMAGE_URL + jsonObject.getString("url"))
                                                    .into(image);
                                            uploadImage = jsonObject.getString("pic");
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

}
