package com.example.tutorpoint.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutorpoint.AddCourse;
import com.example.tutorpoint.R;
import com.example.tutorpoint.modals.ProductImages;

import java.util.ArrayList;

public class ProductImagesDataAdapters extends RecyclerView.Adapter<ProductImagesDataAdapters.ViewHolder> {
    private ArrayList<ProductImages> imageUrls;
    private AddCourse context;

    public ProductImagesDataAdapters(AddCourse context, ArrayList<ProductImages> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public ProductImagesDataAdapters.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_product_image_list_item, viewGroup, false);
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
        viewHolder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.removeImageFromList(viewHolder.img.getTag().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        ImageButton removeBtn;
        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
            removeBtn = view.findViewById(R.id.deleteImage);
        }
    }


}