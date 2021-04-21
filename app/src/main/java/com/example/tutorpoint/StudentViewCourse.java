package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.adapters.ViewProductImagesDataAdapters;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.ProductImages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class StudentViewCourse extends AppCompatActivity {


    boolean alreadyEnrollled = false;
    Intent intent;
    Course course;
    String availability="";

    int studentscount = 0;
    double rating = 0.0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_course);
        course = (Course) getIntent().getSerializableExtra("course");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ((TextView)(findViewById(R.id.title))).setText(course.title);
        ((TextView)(findViewById(R.id.desc))).setText(course.desc);
        ((TextView)(findViewById(R.id.price))).setText("Rate per hour: $" + course.charges_per_hour);

        try {
            JSONArray timearr = new JSONArray(course.timeSlots);
            for(int i=0;i<timearr.length();i++)
            {
                availability += timearr.getJSONObject(i).getString("day")  + " : " + timearr.getJSONObject(i).getString("time") + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ProductImages> imageUrlList = new ArrayList<>();
        for (String ia : course.images) {
            imageUrlList.add(new ProductImages(-1, ia));
        }
        if (imageUrlList.size() == 0) {
            imageUrlList.add(new ProductImages(-1, "https://enhancedperformanceinc.com/wp-content/uploads/2017/10/product-dummy.png"));
        }



        RecyclerView recyclerView = findViewById(R.id.recyclerViewImgs);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ViewProductImagesDataAdapters dataAdapter = new ViewProductImagesDataAdapters(this, imageUrlList);
        recyclerView.setAdapter(dataAdapter);

        getReviews();

        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJoin();
            }
        });
    }

    private void getReviews() {
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getEnrollmentByCourse?courseId="+course.id;

        JsonObjectRequest jsonObjReq;
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject obj) {
                        // pDialog.hide();

                        try {

                            JSONArray enrollArr= obj.getJSONArray("enrollment");
                            for(int i=0;i<enrollArr.length();i++){
                                JSONObject enr = enrollArr.getJSONObject(i);
                                if(enr.getJSONObject("student_id").getString("_id").equals(new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id")))
                                {
                                    Toast.makeText(getApplicationContext(), "Already Enrolled", Toast.LENGTH_SHORT).show();
                                    ((RelativeLayout)findViewById(R.id.buttons)).setVisibility(View.GONE);
                                }

                                if(enr.getString("status").equals("completed")) {
                                    if (i == 0) {
                                        ((TextView)findViewById(R.id.reviewtitle)).setVisibility(View.VISIBLE);
                                        ((TextView)findViewById(R.id.studetname1)).setVisibility(View.VISIBLE);
                                        ((TextView)findViewById(R.id.studentsays1)).setVisibility(View.VISIBLE);
                                        ((TextView)findViewById(R.id.comment1)).setVisibility(View.VISIBLE);


                                        ((TextView)findViewById(R.id.studetname1)).setText(enr.getJSONObject("student_id").getString("name"));
                                        ((TextView)findViewById(R.id.comment1)).setText("''"+enr.getString("student_comment")+"''");
                                    }
                                    if (i == 1) {
                                        ((TextView)findViewById(R.id.studetname2)).setVisibility(View.VISIBLE);
                                        ((TextView)findViewById(R.id.studentsays2)).setVisibility(View.VISIBLE);
                                        ((TextView)findViewById(R.id.comment2)).setVisibility(View.VISIBLE);

                                        ((TextView)findViewById(R.id.studetname2)).setText(enr.getJSONObject("student_id").getString("name"));
                                        ((TextView)findViewById(R.id.comment2)).setText("''"+enr.getString("student_comment")+"''");
                                    }
                                    studentscount++;
                                    rating += enr.getDouble("student_rating");
                                }
                            }
                            if (studentscount > 0) {
                                rating = rating / studentscount;
                                ((TextView)findViewById(R.id.rating)).setText("Student Ratings: " + String.valueOf(rating)+"/5");
                            }
                            ((TextView)findViewById(R.id.availability)).setText(availability);
                            ((TextView)findViewById(R.id.numenr)).setText("No of students enrolled: "+String.valueOf(studentscount));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Sorry, Some error occured", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Sorry, Some error occured", Toast.LENGTH_LONG).show();
                // hide the progress dialog
                // pDialog.hide();
            }
        });

        // Adding request to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }


    private void requestJoin()
    {
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/createEnrollment";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("course_id",course.id);
            jsonObject.put("student_id",new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id"));
            jsonObject.put("tutor_id",course.tutorid);
            jsonObject.put("hourse_completed",0);
            jsonObject.put("status","requested");
            jsonObject.put("student_rating",0);
            jsonObject.put("student_comment","");
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
                        Toast.makeText(StudentViewCourse.this, "Request sent", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(StudentViewCourse.this, "Error Occurred", Toast.LENGTH_LONG).show();
                        finish();
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
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}