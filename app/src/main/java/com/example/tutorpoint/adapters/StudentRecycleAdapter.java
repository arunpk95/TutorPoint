package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutorpoint.R;
import com.example.tutorpoint.helpers.SharedPreferenceHelper;
import com.example.tutorpoint.modals.Student;

import java.util.ArrayList;

public class StudentRecycleAdapter extends RecyclerView.Adapter<StudentRecycleAdapter.StudentHolder> {

    ArrayList<Student> students;
    Context context;
    public StudentRecycleAdapter(Context ct, ArrayList<Student> p){
        context = ct;
        this.students = p;
    }
    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tutor_student_cell,parent,false);
        return new StudentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHolder holder, final int position) {

    }



    @Override
    public int getItemCount() {
        return students.size();
    }


    public class StudentHolder extends RecyclerView.ViewHolder {

        public StudentHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void clear() {
        int size = students.size();
        students.clear();
        notifyItemRangeRemoved(0, size);
    }
}
