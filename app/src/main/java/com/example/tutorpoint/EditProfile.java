package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity {
    JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            obj = new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user",""));
            ((EditText)findViewById(R.id.username)).setText(obj.getString("email"));
            ((EditText)findViewById(R.id.password)).setText(obj.getString("password"));
            ((EditText)findViewById(R.id.name)).setText(obj.getString("name"));
            ((EditText)findViewById(R.id.bio)).setText(obj.getString("bio"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }


    private void updateProfile() {
        String email,password,name;
        email = ((EditText)findViewById(R.id.username)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        name =  ((EditText)findViewById(R.id.name)).getText().toString();

        if(password.length() == 0 || email.length()==0 || name.length()==0)
        {
            Toast.makeText(getApplicationContext(),"All fields are required!!!",Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/updateUser";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating profile...");
        pDialog.show();

        JSONObject studentObj = new JSONObject();
        try {
            studentObj.put("_id",obj.getString("_id"));
            studentObj.put("name",name);
            studentObj.put("email",email);
            studentObj.put("password",password);
            studentObj.put("bio", ((EditText)findViewById(R.id.bio)).getText().toString());
            studentObj.put("country",((AppCompatSpinner)findViewById(R.id.spinner1)).getSelectedItem().toString());
            studentObj.put("languages", new JSONArray().put("English"));
            studentObj.put("userType",obj.getInt("userType"));
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

                                Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                                SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"user",studentObj.toString());
                                finish();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_LONG).show();
                                finish();
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