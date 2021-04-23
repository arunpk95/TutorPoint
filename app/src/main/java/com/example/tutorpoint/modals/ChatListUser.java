package com.example.tutorpoint.modals;

public class ChatListUser {
    public  String id, name, lastMsg;

    public ChatListUser(){

    }

    public ChatListUser(String id, String name, String lastMsg) {
        this.id = id;
        this.name = name;
        this.lastMsg = lastMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
