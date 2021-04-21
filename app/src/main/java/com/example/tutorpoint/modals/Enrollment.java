package com.example.tutorpoint.modals;

import java.io.Serializable;
import java.util.Date;

public class Enrollment implements Serializable {
    public String status, student_comment;
    public int hours_completed;
    public Double student_rating;
    public String tutorobj, courseobj, studentobj, id;

    public Enrollment( String status, String student_comment, int hours_completed, Double student_rating, String tutorobj, String courseobj, String studentobj, String id) {
        this.status = status;
        this.student_comment = student_comment;
        this.hours_completed = hours_completed;
        this.student_rating = student_rating;
        this.tutorobj = tutorobj;
        this.courseobj = courseobj;
        this.studentobj = studentobj;
        this.id=id;
    }
}
