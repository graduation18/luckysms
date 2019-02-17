package com.example.gaber.luckysms.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.model.contact_model;
import com.example.gaber.luckysms.model.sms_messages_model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class messages_list_adapter extends RecyclerView.Adapter<messages_list_adapter.MyViewHolder> {

private Context context;
private List<sms_messages_model> contact_list;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_sms,date_sms;
        public MyViewHolder(View view) {
        super(view);
            text_sms=(TextView) view.findViewById(R.id.text_sms);
            date_sms=(TextView) view.findViewById(R.id.date_sms);

    }
}


    public messages_list_adapter(Context context, List<sms_messages_model> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sms_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        sms_messages_model data = contact_list.get(position);
        Date date=new Date(data.date);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String dateText = df2.format(date);
         holder.text_sms.setText(data.body);
         holder.date_sms.setText(dateText);
        ContentValues values = new ContentValues();
        values.put("read", true);
        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + data.id, null);

        Log.w("kjshdakasd", String.valueOf(data.id));




    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }





}


