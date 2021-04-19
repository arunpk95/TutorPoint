package com.example.tutorpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutorpoint.AddCourse;
import com.example.tutorpoint.R;
import com.example.tutorpoint.modals.Course;

import java.io.Serializable;
import java.util.ArrayList;

public class TutorHomeRecycleAdapter extends RecyclerView.Adapter<TutorHomeRecycleAdapter.homeHolder> implements Serializable {
    ArrayList<Course> courses;
    Context context;
    public TutorHomeRecycleAdapter(Context ct, ArrayList<Course> p){
        context = ct;
        this.courses = p;
    }
    @NonNull
    @Override
    public homeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tutor_home_course_cell,parent,false);
        return new homeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull homeHolder holder, final int position) {
        String imageURL = "https://enhancedperformanceinc.com/wp-content/uploads/2017/10/product-dummy.png";
        if(courses.get(position).images.length != 0)
        {
            imageURL = courses.get(position).images[0];
        }
        Glide.with(context).load(imageURL).into(holder.image);
        holder.image.setTag(imageURL);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddCourse.class);
                i.putExtra("pid",courses.get(position));

                context.startActivity(i);
            }
        });
        holder.textView.setText(courses.get(position).title);
        holder.category.setText("Category: "+ courses.get(position).category);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }


    public class homeHolder extends RecyclerView.ViewHolder implements Serializable{
        ImageView image;
        TextView textView,  category;
        public homeHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productImage);
            textView = itemView.findViewById(R.id.courseTitle);
            category = itemView.findViewById(R.id.category);
        }
    }
    public void clear() {
        int size = courses.size();
        courses.clear();
        notifyItemRangeRemoved(0, size);
    }
}
