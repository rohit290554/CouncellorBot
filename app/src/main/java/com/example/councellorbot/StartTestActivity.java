package com.example.councellorbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class StartTestActivity extends AppCompatActivity {
    Spinner spin;
    Button start;
    String choice[] ={"Select Field","10th","Medical","Engineering","Law"};
    String ch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spin = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,choice);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ch = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ch.equalsIgnoreCase("Medical")) {
                    startActivity(new Intent(StartTestActivity.this,MedicalEntranceTestActivity.class));
                } else if(ch.equalsIgnoreCase("Engineering")) {
                    startActivity(new Intent(StartTestActivity.this,EngineeringEntranceTestActivity.class));
                } else if(ch.equalsIgnoreCase("Law")) {
                    startActivity(new Intent(StartTestActivity.this,LawEntranceTestActivity.class));
                } else if(ch.equalsIgnoreCase("10th")) {
                    startActivity(new Intent(StartTestActivity.this,TenEntranceTestActivity.class));
                }
                else {
                    Toast.makeText(StartTestActivity.this, "Select Feild First", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
