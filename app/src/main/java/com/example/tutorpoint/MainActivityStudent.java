package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.adapters.StudentHomeRecycleAdapter;
import com.example.tutorpoint.adapters.StudentRecycleAdapter;
import com.example.tutorpoint.adapters.TutorHomeRecycleAdapter;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivityStudent extends AppCompatActivity {
    Toolbar searchToolBar;
    StudentHomeRecycleAdapter homeAdapter,enrolledAdapter;
    RecyclerView homeRecycleView, enrolledCoursesView;
    ProgressBar progress_circular;

    RadioGroup enrolledFilterRG;

    ArrayList<Course> searchcourses  = new ArrayList<>(),  enrolledCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        searchToolBar = (Toolbar) findViewById(R.id.toolbar);



        setTitle("Search Courses");

        homeRecycleView = findViewById(R.id.recycleView);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.GONE);
        enrolledCoursesView = findViewById(R.id.enrolledCoursesView);
        enrolledFilterRG = findViewById(R.id.radioGroup);

        removeAllViews();

        try {
            getCourses();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTitle("Your Courses");
                try {
                    removeAllViews();
                    getCourses();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Your Notifications");
                try {
                    removeAllViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Your Chats");
            }
        });
        findViewById(R.id.courses).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setTitle("Your Students");
                try {
                    setTitle("Your Enrollments");
                    removeAllViews();
                    getEnrollments();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutor_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            // do something here
            SharedPreferenceHelper.setSharedPreferenceString(getApplicationContext(),"user","");
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getCourses() throws JSONException {

        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getCourses";
        JSONObject filters = new JSONObject();
        filters.put("fromPrice","");
        filters.put("toPrice","");
        filters.put("category","");
        String requestBody = filters.toString();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray jsonArray = new JSONObject(s).getJSONArray("courses");
                            searchcourses.clear();
                            if (jsonArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No courses found.", Toast.LENGTH_LONG).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    JSONArray imgObjArr = obj.getJSONArray("images");
                                    //JSONArray imgObjArr = new JSONArray();
                                    String[] imgArr = new String[imgObjArr.length()];
                                    for(int k=0; k < imgObjArr.length(); k++) {
                                        imgArr[k] = imgObjArr.getString(k);
                                    }
                                    //    public Course(Date created_on, Date updated_on, User tutor, String status, String category, int hours_expectd, int student_capacity, int charges_per_hour, JSONArray timeSlots,
                                    //                  String[] images, String videoLink) {
                                    Course c = new Course(
                                            obj.getString("title"),
                                            obj.getString("description"),
                                            new Date(),
                                            new Date(),
                                            new User(),
                                            obj.getString("status"),
                                            obj.getString("category"),
                                            obj.getInt("hours_expected"),
                                            obj.getInt("students_capacity"),
                                            obj.getInt("charge_per_hour"),
                                            obj.getJSONArray("time_slot").toString(),
                                            imgArr,
                                            obj.getString("video_link")
                                    );
                                    searchcourses.add(c);


                                }
                            }
                            updateHomeRecycler();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progress_circular.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }){
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


    public void getEnrollments()
    {
        enrolledCourses = searchcourses;
        updateEnrolledRecycle();
    }

    public void removeAllViews(){
        homeRecycleView.setVisibility(View.GONE);
        searchToolBar.setVisibility(View.GONE);



        //enrolled courses
        enrolledCoursesView.setVisibility(View.GONE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.GONE);
    }




    private void updateHomeRecycler() {
        homeRecycleView.setVisibility(View.VISIBLE);
        searchToolBar.setVisibility(View.VISIBLE);
        homeRecycleView.removeAllViews();
        homeAdapter = new StudentHomeRecycleAdapter(this, searchcourses);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        homeRecycleView.setLayoutManager(gridLayoutManager);
        homeRecycleView.setAdapter(homeAdapter);
    }

    private void updateEnrolledRecycle() {


        enrolledCoursesView.setVisibility(View.VISIBLE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.VISIBLE);


        enrolledCoursesView.removeAllViews();
        enrolledAdapter = new StudentHomeRecycleAdapter(this, searchcourses);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        enrolledCoursesView.setLayoutManager(gridLayoutManager);
        enrolledCoursesView.setAdapter(enrolledAdapter);
    }

}