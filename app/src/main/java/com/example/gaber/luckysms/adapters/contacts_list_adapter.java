package com.example.gaber.luckysms.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.model.contact_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class contacts_list_adapter extends RecyclerView.Adapter<contacts_list_adapter.MyViewHolder>  {

private Context context;
private List<contact_model> contact_list;

    public void filterList(ArrayList<contact_model> filteredList) {
        contact_list = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,number;
        public ImageView image;
        public MyViewHolder(View view) {
        super(view);
        name=(TextView) view.findViewById(R.id.contact_name);
        number=(TextView) view.findViewById(R.id.contact_number);
        image=(ImageView)view.findViewById(R.id.contact_image);

    }
}



    public contacts_list_adapter(Context context, List<contact_model> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        contact_model data = contact_list.get(position);
         holder.name.setText(data.contact_name);
         holder.number.setText(data.contact_phoneNumber);
        /*Bitmap bit_thumb = null;
        try {
            if (data.contact_image != null) {
                bit_thumb = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(data.contact_image));
            } else {
                Log.e("No Image Thumb", "--------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
         Picasso.with(context)
                .load(data.contact_image)
                .placeholder(R.drawable.nothumbinal)
                .transform(new PicassoCircleTransformation())
                .into(holder.image, new Callback() {
            @Override
            public void onSuccess() {}
            @Override public void onError() {
                Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
            }
        });




    }


    @Override
    public int getItemCount() {
        return contact_list.size();
    }





}


