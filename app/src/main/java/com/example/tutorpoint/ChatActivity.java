package com.example.tutorpoint;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tutorpoint.adapters.ChatAdapter;
import com.example.tutorpoint.modals.ChatDetails;
import com.example.tutorpoint.modals.ChatListUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText editText;
    Button send;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private DatabaseReference userReference1;
    private DatabaseReference userReference2;
    private ListView listView;
    private List<ChatDetails> chatDetails = new ArrayList<>();

    private List<ChatListUser> chatUsers1 = new ArrayList<>();
    private List<ChatListUser> chatUsers2 = new ArrayList<>();

    boolean isInitalLoad1;
    boolean isInitalLoad2;

    String firstUser;
    String secondUser;
    String firstUserName;
    String secondUserName;

    public ChatActivity() {
         firstUser = "111";
         secondUser = "110";
         firstUserName = "";
         secondUserName= "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.edittext_chatbox);
        send = findViewById(R.id.button_chatbox_send);
        listView = findViewById(R.id.messageList);

        firstUser = String.valueOf(getIntent().getStringExtra("firstUserId"));
        secondUser = String.valueOf(getIntent().getStringExtra("secondUserId"));

        firstUserName = String.valueOf(getIntent().getStringExtra("firstUserName"));
        secondUserName = String.valueOf(getIntent().getStringExtra("secondUserName"));

        this.getSupportActionBar().setTitle(getIntent().getStringExtra("targetUserName"));

        databaseReference1 = FirebaseDatabase.getInstance().getReference(firstUser + "_" + secondUser);
        databaseReference2 = FirebaseDatabase.getInstance().getReference(secondUser + "_" + firstUser);
        userReference1 = FirebaseDatabase.getInstance().getReference(firstUser);
        userReference2 = FirebaseDatabase.getInstance().getReference(secondUser);

        chatUsers1 = new ArrayList<>();
        chatUsers2 = new ArrayList<>();
        isInitalLoad1 = true;
        isInitalLoad2 = true;

        getSupportActionBar().setTitle(secondUserName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChatAdapter chatAdapter = new ChatAdapter(this, chatDetails, firstUser, secondUser);
        listView.setAdapter(chatAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatDetails chatDetails = new ChatDetails();
                chatDetails.setMessage(editText.getText().toString());
                chatDetails.setUser(firstUser);

                Calendar calendar = Calendar.getInstance();
                String time  = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                chatDetails.setTime(time);

                chatDetails.setDate(calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));

                databaseReference1.push().setValue(chatDetails);
                databaseReference2.push().setValue(chatDetails);

                ChatListUser userRef1Data = new ChatListUser(secondUser, secondUserName, chatDetails.getMessage());
                ChatListUser userRef2Data = new ChatListUser(firstUser, firstUserName, chatDetails.getMessage());

//                userReference1.child("lastMessage").setValue(userRef1Data);
//                userReference2.child("lastMessage").setValue(userRef2Data);

               // userReference1.push().setValue(userRef1Data);

                update(secondUser, chatUsers1, userRef1Data);
                update(firstUser, chatUsers2, userRef2Data);

                userReference1.setValue(chatUsers1);
                userReference2.setValue(chatUsers2);

                editText.setText("");
            }
        });


        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatDetails chatDetail = snapshot.getValue(ChatDetails.class);
                chatDetails.add(chatDetail);
                listView.invalidateViews();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isInitalLoad1) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        chatUsers1.add(child.getValue(ChatListUser.class));
                    }
                    isInitalLoad1 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isInitalLoad2) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        chatUsers2.add(child.getValue(ChatListUser.class));
                    }
                    isInitalLoad2 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update(String id, List<ChatListUser> users, ChatListUser user){
        int response = isExist(id, users);
        if(response == -1) {
            users.add(user);
        } else {
            users.set(response, user);
        }
    }

    public int isExist(String id, List<ChatListUser> users) {
        for(int i=0; i< users.size(); i++) {
            if(users.get(i).id.equals(id)) {
                return i;
            }
        }
        return -1;
    }
}