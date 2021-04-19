package com.example.tutorpoint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        skipLogin();
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();

            }
        });
        findViewById(R.id.openSignups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                finish();
            }
        });
    }

    private void skipLogin() {
        if(!SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","").equals(""))
        {
            try {
                JSONObject userObj = new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user",""));
                if(userObj.getInt("userType") == 1)
                {
                    startActivity(new Intent(getApplicationContext(), MainActivityStudent.class));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(), MainActivityTutor.class));
                }
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void attemptLogin() {
        final EditText emailET = findViewById(R.id.username);
        EditText passwordET = findViewById(R.id.password);

        if(emailET.getText().toString().length() ==0 || passwordET.getText().toString().length() ==0 )
        {
            Toast.makeText(getApplicationContext(),"Both fields are required!!",Toast.LENGTH_LONG).show();
            return;
        }
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/login";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Logging In...");
        pDialog.show();
        int userType= ((RadioButton) findViewById(((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId())).getText().toString().equals("Tutor")?0:1; ;

        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("email",emailET.getText().toString());
            bodyObj.put("password", passwordET.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq;
        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, bodyObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        try {
                            if(response.getString("status").equals("success"))
                            {

                                Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_LONG).show();
                                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"user",response.getJSONObject("user").toString());
                                if(response.getJSONObject("user").getInt("userType") == 1)
                                {
                                    startActivity(new Intent(getApplicationContext(), MainActivityStudent.class));
                                }
                                else
                                {
                                    startActivity(new Intent(getApplicationContext(), MainActivityTutor.class));
                                }
                                finish();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }
}