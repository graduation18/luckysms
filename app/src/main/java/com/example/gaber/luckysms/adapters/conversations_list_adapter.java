package com.example.gaber.luckysms.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.model.contact_model;
import com.example.gaber.luckysms.model.sms_conversation_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class conversations_list_adapter extends RecyclerView.Adapter<conversations_list_adapter.MyViewHolder> {

private Context context;
private List<sms_conversation_model> contact_list;
    public void filterList(ArrayList<sms_conversation_model> filteredList) {
        contact_list = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView snippet,snippet_date,contact_name;
        public ImageView contact_image;
        public LinearLayout layout;
        public MyViewHolder(View view) {
        super(view);
            snippet=(TextView) view.findViewById(R.id.snippet);
            snippet_date=(TextView) view.findViewById(R.id.snippet_date);
            contact_name=(TextView) view.findViewById(R.id.contact_name);
            contact_image=(ImageView)view.findViewById(R.id.contact_image);
            layout=(LinearLayout)view.findViewById(R.id.layout);

    }
}


    public conversations_list_adapter(Context context, List<sms_conversation_model> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conversation_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        sms_conversation_model data = contact_list.get(position);
        if (data.snippet.length()>25){
            String upToNCharacters = data.snippet.substring(0, Math.min(data.snippet.length(), 22));
            holder.snippet.setText(upToNCharacters+"...");
        }else {
            holder.snippet.setText(data.snippet);
        }
        holder.snippet_date.setText(data.dateText);
        if (data.boolean_seen.contains("1")){
            holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else {
            holder.layout.setBackgroundColor(Color.parseColor("#81979797"));
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
                        Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
                    }
                });





    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }




}


