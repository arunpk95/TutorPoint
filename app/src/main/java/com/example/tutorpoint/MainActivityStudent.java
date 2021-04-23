package com.example.tutorpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.adapters.ChatHomeAdapter;
import com.example.tutorpoint.adapters.StudentEnrolledAdapter;
import com.example.tutorpoint.adapters.StudentHomeRecycleAdapter;
import com.example.tutorpoint.adapters.StudentRecycleAdapter;
import com.example.tutorpoint.adapters.StudentRequestedAdapter;
import com.example.tutorpoint.adapters.TutorHomeRecycleAdapter;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.ChatListUser;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.Enrollment;
import com.example.tutorpoint.modals.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivityStudent extends AppCompatActivity {
    Toolbar searchToolBar;
    StudentHomeRecycleAdapter homeAdapter;
    StudentEnrolledAdapter enrolledAdapter;
    RecyclerView homeRecycleView, enrolledCoursesView, requestedRecycler, recyclerViewChat;
    ProgressBar progress_circular;

    RadioGroup enrolledFilterRG;

    StudentRequestedAdapter studentRequestedAdapter;

    ArrayList<Course> searchcourses  = new ArrayList<>(),  enrolledCourses = new ArrayList<>();
    ArrayList<Enrollment> requestedLists = new ArrayList<>(), myenrollments = new ArrayList<>();
    ArrayList<ChatListUser> chatListUserArrayList = new ArrayList<>();

    ChatHomeAdapter chatHomeAdapter;


    SearchView searchView;

    DatabaseReference chatListRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        searchToolBar = (Toolbar) findViewById(R.id.toolbar);



        setTitle("Search Courses");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Silver)));

        homeRecycleView = findViewById(R.id.recycleView);
        progress_circular = findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.GONE);
        enrolledCoursesView = findViewById(R.id.enrolledCoursesView);
        enrolledFilterRG = findViewById(R.id.radioGroup);
        searchView = findViewById(R.id.searchView);
        requestedRecycler = findViewById(R.id.recycleViewRequested);
        recyclerViewChat = findViewById(R.id.recycleViewChat);

        removeAllViews();

        try {
            getCourses();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });

        findViewById(R.id.filterPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Filter.class));
            }
        });

        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTitle("Search Courses");
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
                setTitle("Requests Sent");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Silver)));
                try {
                    removeAllViews();
                    getMyRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Your Chats");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BlueGrey)));
                try {
                    removeAllViews();
                    //getChats();
                    JSONObject currentUser = new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(), "user", ""));
                    chatListRef = FirebaseDatabase.getInstance().getReference(String.valueOf(currentUser.get("_id")));

                    chatListRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            chatListUserArrayList.clear();
                            for (DataSnapshot child : snapshot.getChildren()) {
                                chatListUserArrayList.add(child.getValue(ChatListUser.class));
                            }
                            updateChatRecycler();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.courses).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BlueGrey)));
                try {
                    setTitle("Your Enrollments");
                    removeAllViews();
                    getEnrollments();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((RadioGroup)findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                doEnrollFilter();
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Search Courses");
        removeAllViews();

        try {
            getCourses();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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


    private void performSearch(String query) {
        ArrayList<Course> filteredList = new ArrayList<>();
        if(query == null || query.length() == 0){
            updateHomeRecycler(searchcourses);
        }else{
            ArrayList<Course> filterProduct = new ArrayList(searchcourses);
            String filterPattern = query.toString().toLowerCase().trim();
            for (Course item : filterProduct) {
                if (item.title.toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
            updateHomeRecycler(filteredList);
        }
    }


    private void getCourses() throws JSONException {

        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getCourses";
        JSONObject filters = new JSONObject();
        filters.put("fromPrice",SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"filterfromprice",""));
        filters.put("toPrice",SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"filtertoprice",""));
        filters.put("category",SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"filtercategory",""));
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
                                    c.id  = obj.getString("_id");
                                    c.tutorid = new JSONObject(obj.getString("tutor_id")).getString("_id");
                                    c.tutorName = new JSONObject(obj.getString("tutor_id")).getString("name");
                                    searchcourses.add(c);


                                }
                            }
                            updateHomeRecycler(searchcourses);

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

    void getMyRequests() throws JSONException{

        progress_circular.setVisibility(View.VISIBLE);

        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getEnrollmentByStudent?studentId="+new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id");

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
                            updateRequestedRecycler();

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


    public void getEnrollments()
    {

        progress_circular.setVisibility(View.VISIBLE);
        String url = null;
        try {
            url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/getEnrollmentByStudent?studentId="+new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(getApplicationContext(),"user","")).getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq;
        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject object) {
                        // pDialog.hide();

                        progress_circular.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray jsonArray = object.getJSONArray("enrollment");
                            myenrollments.clear();
                            if (jsonArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No courses found.", Toast.LENGTH_LONG).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    if(!obj.getString("status").equals("requested"))
                                    {
                                        myenrollments.add(new Enrollment(
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
                            doEnrollFilter();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void getChats()
    {
        chatListUserArrayList.clear();
        chatListUserArrayList.add(new ChatListUser("ajshdbfaksjhdbfjkas", "Arun", "Hello Arun"));
        chatListUserArrayList.add(new ChatListUser("ajshdbfaksjhdbfjkas", "Arun", "Hello Arun"));
        chatListUserArrayList.add(new ChatListUser("ajshdbfaksjhdbfjkas", "Arun", "Hello Arun"));
        chatListUserArrayList.add(new ChatListUser("ajshdbfaksjhdbfjkas", "Arun", "Hello Arun"));

        updateChatRecycler();
    }


    public void removeAllViews(){
        homeRecycleView.setVisibility(View.GONE);
        searchToolBar.setVisibility(View.GONE);


        recyclerViewChat.setVisibility(View.GONE);

        requestedRecycler.setVisibility(View.GONE);

        //enrolled courses
        enrolledCoursesView.setVisibility(View.GONE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.GONE);
    }

    public void doEnrollFilter()
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
        for(Enrollment s:myenrollments)
        {
            if(option.equals("all"))
            {
                finalList = myenrollments;
                break;
            }
            else if(option.equals(s.status))
            {
                finalList.add(s);
            }
        }

            updateEnrolledRecycle(finalList);

    }



    private void updateHomeRecycler(ArrayList<Course> c) {
        homeRecycleView.setVisibility(View.VISIBLE);
        searchToolBar.setVisibility(View.VISIBLE);
        homeRecycleView.removeAllViews();
        homeAdapter = new StudentHomeRecycleAdapter(this, c);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        homeRecycleView.setLayoutManager(gridLayoutManager);
        homeRecycleView.setAdapter(homeAdapter);
    }

    private void updateEnrolledRecycle(ArrayList<Enrollment> c) {


        enrolledCoursesView.setVisibility(View.VISIBLE);
        ((RadioGroup)findViewById(R.id.radioGroup)).setVisibility(View.VISIBLE);
        ((RadioButton)findViewById(R.id.radioButton1)).setSelected(true);

        enrolledCoursesView.removeAllViews();

        enrolledAdapter = new StudentEnrolledAdapter(this, c);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        enrolledCoursesView.setLayoutManager(gridLayoutManager);
        enrolledCoursesView.setAdapter(enrolledAdapter);



    }


    private void updateRequestedRecycler() {

        requestedRecycler.setVisibility(View.VISIBLE);

        requestedRecycler.removeAllViews();
        studentRequestedAdapter = new StudentRequestedAdapter(this, requestedLists);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        requestedRecycler.setLayoutManager(gridLayoutManager);
        requestedRecycler.setAdapter(studentRequestedAdapter);
    }

    private void updateChatRecycler()
    {
        ((RecyclerView)findViewById(R.id.recycleViewChat)).setVisibility(View.VISIBLE);

        recyclerViewChat.removeAllViews();
        chatHomeAdapter = new ChatHomeAdapter(this, chatListUserArrayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerViewChat.setLayoutManager(gridLayoutManager);
        recyclerViewChat.setAdapter(chatHomeAdapter);

    }

}