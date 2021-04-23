package com.example.tutorpoint.modals;

public class ChatDetails {
    private String message;
    private String user;
    private String date;
    private String time;


    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
