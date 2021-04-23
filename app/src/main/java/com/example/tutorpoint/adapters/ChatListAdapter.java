package com.example.tutorpoint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tutorpoint.R;
import com.example.tutorpoint.modals.ChatUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatUser> {

    private List<ChatUser> chatUsers;
    private DatabaseReference userReference;

    Context context;

    public ChatListAdapter(Context ct, List<ChatUser> chatUsers){
        super(ct, -1, chatUsers);
        context = ct;
        this.chatUsers = chatUsers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.chatview_list_item,parent,false);

        System.out.println("USR"+chatUsers.get(position).getUserName());
        System.out.println("MESSG"+chatUsers.get(position).getLastMessage());

        TextView message = convertView.findViewById(R.id.message);
        message.setText(chatUsers.get(position).getLastMessage());

        TextView name = convertView.findViewById(R.id.profile_name);
        name.setText(chatUsers.get(position).getUserName());

        return convertView;
    }


}
