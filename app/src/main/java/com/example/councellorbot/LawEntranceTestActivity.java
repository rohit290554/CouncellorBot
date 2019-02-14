package com.example.councellorbot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LawEntranceTestActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_question;

    private LawQuestion question = new LawQuestion();

    private String answer,user_id,test,status,msg;
    private int questionLength = question.questions.length;
    Button btn_one, btn_two, btn_three, btn_four;
    int count =0,marks=0,j=0;
    ArrayList<Integer> list;
    UserSessionManager session;
    private static final String API_URL = Config.ROOT_URL+"Api.php?apicall=recordmarks";
    ProgressDialog progressDialog;

    Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineering_entrance_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new UserSessionManager(getApplicationContext());
        // Check user login
        // If User is not logged in , This will redirect user to LoginActivity.
        if (session.checkLogin()) {
            finish();
            return;
        }
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(UserSessionManager.KEY_USER_ID);
        test = user.get(UserSessionManager.KEY_TEST);


        random = new Random();

        btn_one = findViewById(R.id.btn_one);
        btn_one.setOnClickListener(this);
        btn_two = findViewById(R.id.btn_two);
        btn_two.setOnClickListener(this);
        btn_three = findViewById(R.id.btn_three);
        btn_three.setOnClickListener(this);
        btn_four = findViewById(R.id.btn_four);
        btn_four.setOnClickListener(this);

        tv_question = findViewById(R.id.tv_question);

        list = new ArrayList<Integer>();
        for (int i=0; i<10; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);


        NextQuestion(list.get(j++));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                if(btn_one.getText() == answer) {
                    marks++;
                }
                break;

            case R.id.btn_two:
                if(btn_two.getText() == answer) {
                    marks++;
                }
                break;

            case R.id.btn_three:
                if(btn_three.getText() == answer) {
                    marks++;
                }
                break;

            case R.id.btn_four:
                if(btn_four.getText() == answer) {
                    marks++;
                }
                break;
        }
        count++;
        if(count !=10 && j<=9) {
            NextQuestion(list.get(j++));
        } else {
            if (test.equalsIgnoreCase("null")) {
                TestOver();
            } else {
                double mark = Double.parseDouble(test);
                if(marks>mark) {
                    status = "Improved";
                } else if(marks<mark) {
                    status = "Not Improved";
                } else {
                    status = "No change";
                }
                TestOverWithPreviousMarks();
            }
            storeMarks();
        }
    }

    private void TestOverWithPreviousMarks() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LawEntranceTestActivity.this);
        alertDialogBuilder
                .setTitle("Test Over")
                .setMessage("Your current marks is "+marks+" out of 10 And Your Previous Marks is "+test+". Your Performance has "+status)
                .setCancelable(false)
                .setPositiveButton("Start Test Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), LawEntranceTestActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialogBuilder.show();

    }

    private void TestOver() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LawEntranceTestActivity.this);
        alertDialogBuilder
                .setTitle("Test Over")
                .setMessage("Your marks is "+marks+" out of 10")
                .setCancelable(false)
                .setPositiveButton("Start Test Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), LawEntranceTestActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialogBuilder.show();

    }

    private void NextQuestion(int num) {
        tv_question.setText(question.getQuestion(num));

        btn_one.setText(question.getchoice1(num));
        btn_two.setText(question.getchoice2(num));
        btn_three.setText(question.getchoice3(num));
        btn_four.setText(question.getchoice4(num));

        answer = question.getCorrectAnswer(num);

    }

    public void storeMarks() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Storing Details...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        session.recordMarks(String.valueOf(marks));
                        Log.d("TAG", response.toString());
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            if (obj.getString("error").equalsIgnoreCase("false")) {
                                Toast.makeText(LawEntranceTestActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(LawEntranceTestActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("marks", String.valueOf(marks));
                params.put("field", "3");
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
