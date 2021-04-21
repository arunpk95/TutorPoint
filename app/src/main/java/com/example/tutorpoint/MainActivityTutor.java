package com.example.tutorpoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.adapters.StudentRecycleAdapter;
import com.example.tutorpoint.adapters.TutorHomeRecycleAdapter;
import com.example.tutorpoint.adapters.TutorNotificationAdapter;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.Enrollment;
import com.example.tutorpoint.modals.Student;
import com.example.tutorpoint.modals.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MainActivityTutor extends AppCompatActivity implements Serializable {

    RecyclerView homeRecycleView, studentRecycleView, notificationRecycler;
    ProgressBar progress_circular;
    ArrayList<Course> courses = new ArrayList<>();
    TutorHomeRecycleAdapter homeAdapter;
    StudentRecycleAdapter studentRecycleAdapter;
    TutorNotificationAdapter tutorNotificationAdapter;


    ArrayList<Student> students = new ArrayList<>();

    ArrayList<Enrollment> requestedLists=new ArrayList<>(), mystudents= new ArrayList<>();

    ArrayList<String> coursesSpinner = new ArrayList<>();
    Spinner spin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tutor);

        setTitle("Your Courses");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Silver)));

        homeRecycleView = findViewById(R.id.recycleView);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.GONE);
        studentRecycleView = findViewById(R.id.recycleViewStudents);
        notificationRecycler = findViewById(R.id.recycleViewNotification);
        coursesSpinner.add("All");


         spin = (Spinner) findViewById(R.id.coursesSpinner);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,coursesSpinner);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        removeAllViews();

        try {
            getCourses();
        } catch (JSONException e) {
            e.printStackTrace();
        }





        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                doStudentsFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTitle("Your Courses");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Silver)));
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
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Silver)));
                try {
                    removeAllViews();
                    getNotifications();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.addProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddCourse.class));
            }
        });
        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              setTitle("Your Chats");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BlueGrey)));
            }
        });
        findViewById(R.id.students).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setTitle("Your Students");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BlueGrey)));
                try {
                    removeAllViews();
                    getStudents();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((RadioGroup)findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                doStudentsFilter();
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
        else if(id == R.id.profile)
        {
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void removeAllViews(){
        homeRecycleView.setVisibility(View.GONE);


        //notificationContents
        ((RecyclerView)findViewById(R.id.recycleViewNotification)).setVisibility(View.GONE);



        //studentViewcontents
        studentRecycleView.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textViewCategory)).setVisibility(View.GONE);
        ((Spinner) findViewById(R.id.coursesSpinner)).setVisibility(View.GONE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.GONE);
    }

    private void getCourses() throws JSONException {

        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getCourseByTutor?tutorId="+new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id");

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progress_circular.setVisibility(View.INVISIBLE);
                try {
                    JSONArray jsonArray = new JSONObject(s).getJSONArray("courses");
                    courses.clear();
                    coursesSpinner.clear();
                    coursesSpinner.add("All");
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
                            courses.add(c);

                            coursesSpinner.add(c.title);
                        }
                    }

                    //Creating the ArrayAdapter instance having the country list
                    ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,coursesSpinner);
                    //Setting the ArrayAdapter data on the Spinner
                    spin.setAdapter(aa);
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
        });

        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void getStudents() throws JSONException{
        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getEnrollmentByTutor?tutorId="+new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id");

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray jsonArray = new JSONObject(s).getJSONArray("enrollment");
                            mystudents.clear();
                            if (jsonArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No requests found.", Toast.LENGTH_LONG).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    if(!obj.getString("status").equals("requested"))
                                    {
                                        mystudents.add(new Enrollment(
                                                obj.getString("status"),
                                                obj.getString("student_comment"),
                                                0,
                                                obj.getDouble("student_rating"),
                                                obj.getJSONObject("tutor_id").toString(),
                                                obj.getJSONObject("course_id").toString(),
                                                obj.getJSONObject("student_id").toString(),
                                                obj.getString("_id")

                                        ));
                                    }

                                }
                            }
                            doStudentsFilter();

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
        });

        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void getNotifications() throws JSONException{
        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getEnrollmentByTutor?tutorId="+new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id");

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progress_circular.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray jsonArray = new JSONObject(s).getJSONArray("enrollment");
                            requestedLists.clear();
                            if (jsonArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No requests found.", Toast.LENGTH_LONG).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    if(obj.getString("status").equals("requested"))
                                    {
                                        requestedLists.add(new Enrollment(
                                                obj.getString("status"),
                                                obj.getString("student_comment"),
                                                0,
                                                obj.getDouble("student_rating"),
                                                obj.getJSONObject("tutor_id").toString(),
                                                obj.getJSONObject("course_id").toString(),
                                                obj.getJSONObject("student_id").toString(),
                                                obj.getString("_id")

                                                ));
                                    }

                                }
                            }
                            updateNotificationRecycler();

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
        });

        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }




    public void doStudentsFilter()
    {
        ArrayList<Enrollment> finalList = new ArrayList<>();
        int selectedRadio = ((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        String option = "all";
        switch (selectedRadio)
        {
            case R.id.radioButton1:
                option = "all";
                break;
            case R.id.radioButton2:
                option="enrolled";
                break;
            case R.id.radioButton3:
                option = "completed";
                break;
            case R.id.radioButton4:
                option = "dropped";
                break;
        }
        for(Enrollment s:mystudents)
        {
            if(option.equals("all"))
            {
                finalList = mystudents;
                break;
            }
            else if(option.equals(s.status))
            {
                finalList.add(s);
            }
        }
        if(!((Spinner)(findViewById(R.id.coursesSpinner))).getSelectedItem().toString().equals("All")) {
            ArrayList<Enrollment> finalList1 = new ArrayList<>();
            for (Enrollment e : finalList) {
                try {
                    if((new JSONObject(e.courseobj).getString("title").equals(((Spinner)(findViewById(R.id.coursesSpinner))).getSelectedItem().toString())))
                    {
                        finalList1.add(e);
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
            updateStudentRecycler(finalList1);
        }
        else
        {
            updateStudentRecycler(finalList);
        }
    }

    private void updateHomeRecycler() {
        homeRecycleView.setVisibility(View.VISIBLE);

        homeRecycleView.removeAllViews();
        homeAdapter = new TutorHomeRecycleAdapter(this, courses);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        homeRecycleView.setLayoutManager(gridLayoutManager);
        homeRecycleView.setAdapter(homeAdapter);
    }

    private void updateStudentRecycler(ArrayList<Enrollment> l) {

        studentRecycleView.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textViewCategory)).setVisibility(View.VISIBLE);
        ((Spinner) findViewById(R.id.coursesSpinner)).setVisibility(View.VISIBLE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.VISIBLE);


        studentRecycleView.removeAllViews();
        studentRecycleAdapter = new StudentRecycleAdapter(this, l);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        studentRecycleView.setLayoutManager(gridLayoutManager);
        studentRecycleView.setAdapter(studentRecycleAdapter);
    }

    private void updateNotificationRecycler()
    {
        ((RecyclerView)findViewById(R.id.recycleViewNotification)).setVisibility(View.VISIBLE);

        if(requestedLists.size() == 0)
        {
            Toast.makeText(getApplicationContext(), "No new requests found.", Toast.LENGTH_LONG).show();
        }

        notificationRecycler.removeAllViews();
        tutorNotificationAdapter = new TutorNotificationAdapter(this, requestedLists);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        notificationRecycler.setLayoutManager(gridLayoutManager);
        notificationRecycler.setAdapter(tutorNotificationAdapter);
    }
}