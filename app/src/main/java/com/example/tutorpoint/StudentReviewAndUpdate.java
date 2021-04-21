package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.Enrollment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class StudentReviewAndUpdate extends AppCompatActivity {

    Enrollment enrollment;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_review_and_update);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        setTitle("Review the Course");

        enrollment = (Enrollment)intent.getSerializableExtra("enrollment");

        try {
            ((TextView)findViewById(R.id.title)).setText(new JSONObject(enrollment.courseobj).getString("title"));
            ((TextView)findViewById(R.id.tutor)).setText("by "+new JSONObject(enrollment.tutorobj).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        seekBar = (SeekBar)findViewById(R.id.seekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar1, int i, boolean b) {
                ((TextView)findViewById(R.id.ratingtext)).setText("Your Rating: " + i + "/5");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((Button)findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((EditText)findViewById(R.id.review)).getText().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please provide a review", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateReview();
            }
        });
    }

    private void updateReview() {
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/updateEnrollment";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id",enrollment.id);
            jsonObject.put("status","completed");
            jsonObject.put("student_rating",seekBar.getProgress());
            jsonObject.put("student_comment",((EditText)findViewById(R.id.review)).getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String requestBody = jsonObject.toString();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(getApplicationContext(), "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


        };
        request.setShouldCache(false);
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


}