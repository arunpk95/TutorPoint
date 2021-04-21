package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.example.tutorpoint.AddCourse;
import com.example.tutorpoint.R;
import com.example.tutorpoint.StudentViewCourse;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.Course;
import com.example.tutorpoint.modals.Enrollment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TutorNotificationAdapter  extends RecyclerView.Adapter<TutorNotificationAdapter.NotificationHolder> {
    ArrayList<Enrollment> enrollments;
    Context context;
    public TutorNotificationAdapter(Context ct, ArrayList<Enrollment> p){
        context = ct;
        this.enrollments = p;
    }
    @NonNull
    @Override
    public TutorNotificationAdapter.NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tutor_notification_cell,parent,false);
        return new TutorNotificationAdapter.NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorNotificationAdapter.NotificationHolder holder, final int position) {
        try {

            holder.msg.setText(new JSONObject(enrollments.get(position).studentobj).getString("name") + " wants to enroll for " + new JSONObject(enrollments.get(position).courseobj).getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                }

            });
        holder.enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("enrolled", enrollments.get(position).id, position);
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
        Button chat, reject, enroll;
        TextView msg;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            chat = itemView.findViewById(R.id.chat);
            reject = itemView.findViewById(R.id.reject);
            enroll = itemView.findViewById(R.id.enroll);
            msg = itemView.findViewById(R.id.msg);
        }
    }
    public void clear() {
        int size = enrollments.size();
        enrollments.clear();
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
                        enrollments.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Accepted Student", Toast.LENGTH_LONG).show();
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
