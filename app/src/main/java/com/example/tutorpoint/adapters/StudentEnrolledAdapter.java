package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.MainActivityStudent;
import com.example.tutorpoint.MainActivityTutor;
import com.example.tutorpoint.R;
import com.example.tutorpoint.StudentReviewAndUpdate;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Enrollment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class StudentEnrolledAdapter extends RecyclerView.Adapter<StudentEnrolledAdapter.StudentHolder> {

    ArrayList<Enrollment> students;
    Context context;

    public StudentEnrolledAdapter(Context ct, ArrayList<Enrollment> p) {
        context = ct;
        this.students = p;
    }

    @NonNull
    @Override
    public StudentEnrolledAdapter.StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_enrolled_cell, parent, false);
        return new StudentEnrolledAdapter.StudentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentEnrolledAdapter.StudentHolder holder, final int position) {

        try {
            holder.studentname.setText("Tutor Name: " + new JSONObject(students.get(position).tutorobj).getString("name"));
            holder.coursetitle.setText("Course: " + new JSONObject(students.get(position).courseobj).getString("title"));
            holder.comment.setText("Your Comment: " + students.get(position).student_comment);
            if (students.get(position).student_comment.trim().equals("")) {
                holder.comment.setVisibility(View.GONE);
            } else {
                holder.comment.setVisibility(View.VISIBLE);
            }

            switch (students.get(position).status) {
                case "completed":
                    holder.status.setText("Status: Completed");
                    break;
                case "dropped":
                    holder.status.setText("Status: Dropped");
                    break;
                case "enrolled":
                    holder.status.setText("Status: In Progress");
                    holder.drop.setVisibility(View.VISIBLE);
                    holder.complete.setVisibility(View.VISIBLE);
                    break;
                default:
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("dropped",students.get(position).id,position);
            }
        });
        holder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StudentReviewAndUpdate.class);
                intent.putExtra("enrollment",students.get(position));
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return students.size();
    }


    public class StudentHolder extends RecyclerView.ViewHolder {

        TextView studentname, coursetitle, status, comment;
        Button drop, complete;
        ImageButton chat;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);
            studentname = (TextView) itemView.findViewById(R.id.studentName);
            coursetitle = (TextView) itemView.findViewById(R.id.courseTitle);
            status = (TextView) itemView.findViewById(R.id.courseStatus);
            comment = (TextView) itemView.findViewById(R.id.review);
            chat = (ImageButton) itemView.findViewById(R.id.chat);
            drop = (Button) itemView.findViewById(R.id.drop);
            complete = (Button) itemView.findViewById(R.id.complete);
        }
    }

    public void clear() {
        int size = students.size();
        students.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void updateStatus(String status , String id, int position)
    {
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/updateEnrollment";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id",id);
            jsonObject.put("status",status);
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
                        ((MainActivityStudent)context).removeAllViews();
                        ((MainActivityStudent)context).getEnrollments();

                        Toast.makeText(context, "Enrollment status updated", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
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
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
