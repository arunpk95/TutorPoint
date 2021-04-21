package com.example.tutorpoint.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutorpoint.R;
import com.example.tutorpoint.StudentViewCourse;
import com.example.tutorpoint.modals.ProductImages;
import com.example.tutorpoint.modals.Student;

import java.util.ArrayList;

public class ViewProductImagesDataAdapters extends RecyclerView.Adapter<ViewProductImagesDataAdapters.ViewHolder> {
    private ArrayList<ProductImages> imageUrls;
    private StudentViewCourse context;

    public ViewProductImagesDataAdapters(StudentViewCourse context, ArrayList<ProductImages> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_product_image_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Glide.with(context).load(imageUrls.get(i).url).into(viewHolder.img);
        viewHolder.img.setTag(imageUrls.get(i).url);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }
}
