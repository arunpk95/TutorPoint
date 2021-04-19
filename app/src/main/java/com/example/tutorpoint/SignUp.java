package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tutorpoint.helpers.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();
        findViewById(R.id.openLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUP();
            }
        });
    }


    private void attemptSignUP() {
        String email,password,c_password,name;
        email = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        c_password = ((EditText)findViewById(R.id.password2)).getText().toString();
        name =  ((EditText)findViewById(R.id.name)).getText().toString();
        int userTypeGroup = ((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        int userType= ((RadioButton) findViewById(userTypeGroup)).getText().toString().equals("Tutor")?0:1; ;

        if(password.length() == 0 || c_password.length()==0 || email.length()==0 || name.length()==0)
        {
            Toast.makeText(getApplicationContext(),"All fields are required!!!",Toast.LENGTH_LONG).show();
            return;
        }
        if(!password.equals(c_password))
        {
            Toast.makeText(getApplicationContext(),"Passwords do not match!!!",Toast.LENGTH_LONG).show();
            return;
        }
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/createUser";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Signing up...");
        pDialog.show();

        JSONObject studentObj = new JSONObject();
        try {
            studentObj.put("name",name);
            studentObj.put("email",email);
            studentObj.put("password",password);
            studentObj.put("bio", "");
            studentObj.put("country",((AppCompatSpinner)findViewById(R.id.spinner1)).getSelectedItem().toString());
            studentObj.put("languages", new JSONArray().put("English"));
            studentObj.put("userType",userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, studentObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        try {
                            if(response.getString("status").equals("success"))
                            {
//                                JSONObject responseJson = new JSONObject(response.toString());
//                                JSONObject pollution = responseJson.getJSONObject("data").getJSONObject("current").getJSONObject("pollution");
//                                JSONObject weather = responseJson.getJSONObject("data").getJSONObject("current").getJSONObject("weather");

                                Toast.makeText(getApplicationContext(),"Please Login to continue",Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"User Already Registered",Toast.LENGTH_LONG).show();
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