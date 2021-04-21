package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.tutorpoint.helpers.SharedPreferenceHelper;

public class Filter extends AppCompatActivity {


    Spinner country, category;
    Button reset;
    Button applyFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        country = findViewById(R.id.countries_spinner);
        category = findViewById(R.id.category);
        reset = findViewById(R.id.reset);
        applyFilter = findViewById(R.id.applyFilter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filtercategory","");
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filterCountry","");
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filterfromprice","");
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filtertoprice","");
                finish();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filtercategory",category.getSelectedItem().toString().equals("Uncategorized")?"":category.getSelectedItem().toString());
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filterCountry",country.getSelectedItem().toString().equals("Select Tutor Country")?"":country.getSelectedItem().toString());
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filterfromprice",((EditText)findViewById(R.id.from)).getText().toString());
                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"filtertoprice",((EditText)findViewById(R.id.to)).getText().toString());
                finish();
            }
        });
    }
}