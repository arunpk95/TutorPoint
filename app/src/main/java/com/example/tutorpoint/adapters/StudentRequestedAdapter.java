package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.tutorpoint.ChatActivity;
import com.example.tutorpoint.R;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Enrollment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class StudentRequestedAdapter extends RecyclerView.Adapter<StudentRequestedAdapter.NotificationHolder> {
    ArrayList<Enrollment> enrollments;
    Context context;
    public StudentRequestedAdapter(Context ct, ArrayList<Enrollment> p){
        context = ct;
        this.enrollments = p;
    }
    @NonNull
    @Override
    public StudentRequestedAdapter.NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_requested_cell,parent,false);
        return new StudentRequestedAdapter.NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRequestedAdapter.NotificationHolder holder, final int position) {
        try {

            holder.msg.setText( new JSONObject(enrollments.get(position).courseobj).getString("title") + " by "+ new JSONObject(enrollments.get(position).tutorobj).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                JSONObject currentUser = null;
                try {
                    currentUser = new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(context.getApplicationContext(), "user", ""));
                    intent.putExtra("firstUserId", String.valueOf(currentUser.get("_id")));
                    intent.putExtra("firstUserName", String.valueOf(currentUser.get("name")));
                    intent.putExtra("secondUserId", new JSONObject(enrollments.get(position).tutorobj).getString("_id"));
                    intent.putExtra("secondUserName", new JSONObject(enrollments.get(position).tutorobj).getString("name"));
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEnrollment(enrollments.get(position).id, position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return enrollments.size();
    }


    public class NotificationHolder extends RecyclerView.ViewHolder implements Serializable {
        Button chat, reject;
        TextView msg;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            chat = itemView.findViewById(R.id.chat);
            reject = itemView.findViewById(R.id.reject);
            msg = itemView.findViewById(R.id.msg);
        }
    }
    public void clear() {
        int size = enrollments.size();
        enrollments.clear();
        notifyItemRangeRemoved(0, size);
    }


    public void deleteEnrollment(String id,int position){
        String url = "https://tutor-point-api.herokuapp.com/tutorPoint/api/deleteEnrollment?id="+id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        enrollments.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Request Rejected.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error Occurred.", Toast.LENGTH_LONG).show();

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
        });
        request.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }
}
