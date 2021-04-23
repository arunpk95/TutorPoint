package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorpoint.ChatActivity;
import com.example.tutorpoint.R;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.modals.ChatListUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_list_cell,parent,false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, final int position) {
        holder.name.setText(chatListUsers.get(position).name);
        holder.msg.setText("Last Msg: " + chatListUsers.get(position).lastMsg);
        holder.time.setText("" );
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                JSONObject currentUser = null;
                try {
                    currentUser = new JSONObject(SharedPreferenceHelper.getSharedPreferenceString(context.getApplicationContext(), "user", ""));
                    intent.putExtra("firstUserId", String.valueOf(currentUser.get("_id")));
                    intent.putExtra("firstUserName", String.valueOf(currentUser.get("name")));
                    intent.putExtra("secondUserId", chatListUsers.get(position).id);
                    intent.putExtra("secondUserName", chatListUsers.get(position).name);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
