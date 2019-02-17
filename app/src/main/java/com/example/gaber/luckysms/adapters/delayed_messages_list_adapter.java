package com.example.gaber.luckysms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.model.delayed_messages_model;
import com.example.gaber.luckysms.model.sms_conversation_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class delayed_messages_list_adapter extends RecyclerView.Adapter<delayed_messages_list_adapter.MyViewHolder> {

private Context context;
private List<delayed_messages_model> contact_list;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView snippet,snippet_date,contact_name;
        public ImageView contact_image;
        public RelativeLayout viewBackground, viewForeground;
        public MyViewHolder(View view) {
        super(view);
            snippet=(TextView) view.findViewById(R.id.snippet);
            snippet_date=(TextView) view.findViewById(R.id.snippet_date);
            contact_name=(TextView) view.findViewById(R.id.contact_name);
            contact_image=(ImageView)view.findViewById(R.id.contact_image);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

        }
}


    public delayed_messages_list_adapter(Context context, List<delayed_messages_model> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.delayed_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        delayed_messages_model data = contact_list.get(position);
        holder.snippet.setText(data.snippet);
        Date date=new Date(data.dateText);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String dateText = df2.format(date);
        holder.snippet_date.setText(dateText);

        if (data.contact_name.length()>0){
            holder.contact_name.setText(data.contact_name);
        }else {
            holder.contact_name.setText(data.strAddress);
        }


        Picasso.with(context)
                .load(data.contact_image)
                .placeholder(R.drawable.nothumbinal)
                .transform(new PicassoCircleTransformation())
                .into(holder.contact_image, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override public void onError() {
                    }
                });





    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }




}


