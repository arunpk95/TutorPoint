package com.example.tutorpoint.modals;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Date;

public class Course implements Serializable {
    public Date created_on, updated_on;
    public User tutor;
    public String title, desc, status, category,videoLink;
    public int hours_expectd, student_capacity, charges_per_hour;
    public String timeSlots;
    public String images[];
    public String id,tutorid;


    public Course(String title, String desc, Date created_on, Date updated_on, User tutor, String status, String category, int hours_expectd, int student_capacity, int charges_per_hour, String timeSlots,
                  String[] images, String videoLink) {
        this.created_on = created_on;
        this.updated_on = updated_on;
        this.tutor = tutor;
        this.status = status;
        this.category = category;
        this.hours_expectd = hours_expectd;
        this.student_capacity = student_capacity;
        this.charges_per_hour = charges_per_hour;
        this.timeSlots = timeSlots;
        this.images = images;
        this.videoLink = videoLink;
        this.title = title;
        this.desc = desc;
    }

}
