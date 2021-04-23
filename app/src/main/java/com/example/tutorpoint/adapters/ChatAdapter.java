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
import com.example.tutorpoint.modals.ChatDetails;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatDetails> {


    private List<ChatDetails> chatDetails;
    Context context;
    String firstUser, secondUser;
    String date = null;

    public ChatAdapter(@NonNull Context context, List<ChatDetails> chatDetails, String firstUser, String secondUser) {
        super(context, R.layout.item_message_sent, R.id.text_message_body, chatDetails);
        this.chatDetails = chatDetails;
        this.context = context;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(date == null || !(date.equals(chatDetails.get(position).getDate()))) {
            if(chatDetails.get(position).getUser().equals(firstUser)) {
                convertView = inflater.inflate(R.layout.item_message_sent_with_date, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_message_received_with_date, parent, false);
            }
            TextView dateDisp = convertView.findViewById(R.id.date_template);
            dateDisp.setText(chatDetails.get(position).getDate());
            date = chatDetails.get(position).getDate();
        } else {
            if (chatDetails.get(position).getUser().equals(firstUser)) {
                convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_message_received, parent, false);
            }
        }

        TextView mssgView = convertView.findViewById(R.id.text_message_body);
        TextView timeView = convertView.findViewById(R.id.text_message_time);
        mssgView.setText(chatDetails.get(position).getMessage());
        timeView.setText(chatDetails.get(position).getTime());
        return convertView;
    }
}
