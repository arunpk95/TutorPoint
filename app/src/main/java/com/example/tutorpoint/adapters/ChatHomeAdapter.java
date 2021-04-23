package com.example.tutorpoint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorpoint.R;
import com.example.tutorpoint.helpers.VolleySingleton;
import com.example.tutorpoint.modals.ChatListUser;
import com.example.tutorpoint.modals.Enrollment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChatHomeAdapter extends RecyclerView.Adapter<ChatHomeAdapter.ChatHolder> {

    Context context;
    ArrayList<ChatListUser> chatListUsers;
    public ChatHomeAdapter(Context ct, ArrayList<ChatListUser> c){
        context = ct;
        chatListUsers = c;
    }
    @NonNull
    @Override
    public ChatHomeAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_list_cell,parent,false);
        return new ChatHomeAdapter.ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHomeAdapter.ChatHolder holder, final int position) {
        holder.name.setText(chatListUsers.get(position).name);
        holder.msg.setText("Last Msg: " + chatListUsers.get(position).lastMsg);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return chatListUsers.size();
    }


    public class ChatHolder extends RecyclerView.ViewHolder implements Serializable {
        TextView msg, time, name;
        CardView cardView;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.card);
        }
    }



}
