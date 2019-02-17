package com.example.gaber.luckysms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.activities.MainActivity;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.fragments.hold_messages;
import com.example.gaber.luckysms.model.hold_messages_model;
import com.example.gaber.luckysms.model.sms_conversation_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class hold_messages_list_adapter extends RecyclerView.Adapter<hold_messages_list_adapter.MyViewHolder>
{

private Context context;
private List<hold_messages_model> contact_list;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView snippet,snippet_date,contact_name;
        public ImageView contact_image;
        public RelativeLayout viewBackground, viewForeground;
        public CheckBox checkBox;
        public MyViewHolder(View view) {
        super(view);
            snippet=(TextView) view.findViewById(R.id.snippet);
            snippet_date=(TextView) view.findViewById(R.id.snippet_date);
            contact_name=(TextView) view.findViewById(R.id.contact_name);
            contact_image=(ImageView)view.findViewById(R.id.contact_image);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            checkBox=(CheckBox)view.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (checkBox.isChecked()){

                    int selected_position = getAdapterPosition();
                    contact_list.get(selected_position).marked=true;

            }else {
                 int selected_position = getAdapterPosition();
                    contact_list.get(selected_position).marked=false;

            }

        }
    }


    public hold_messages_list_adapter(Context context, List<hold_messages_model> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hold_message_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        hold_messages_model data = contact_list.get(position);
        holder.snippet.setText(data.snippet);
        holder.checkBox.setChecked(false);
        if (data.marked){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }
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


