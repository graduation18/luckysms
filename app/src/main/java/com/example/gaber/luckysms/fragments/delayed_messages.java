package com.example.gaber.luckysms.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.activities.messages;
import com.example.gaber.luckysms.adapters.autocomplete_adapter;
import com.example.gaber.luckysms.adapters.delayed_messages_list_adapter;
import com.example.gaber.luckysms.adapters.hold_messages_list_adapter;
import com.example.gaber.luckysms.custom.delayed_messages_RecyclerItemTouchHelper;
import com.example.gaber.luckysms.custom.hold_messages_RecyclerItemTouchHelper;
import com.example.gaber.luckysms.db.database_operations;
import com.example.gaber.luckysms.model.autocomplete_contact_model;
import com.example.gaber.luckysms.model.delayed_messages_model;
import com.example.gaber.luckysms.model.hold_messages_model;
import com.example.gaber.luckysms.services.CustomAlarmReceiver;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.services.SMSReceiver;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static android.content.Context.ALARM_SERVICE;

public class delayed_messages extends Fragment implements delayed_messages_RecyclerItemTouchHelper.RecyclerItemTouchHelperListener  {
    View view;
    private List<delayed_messages_model> contact_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private delayed_messages_list_adapter data_adapter;
    private Toolbar mToolbar;
    private TextView add_message;
    private database_operations db;
    private CustomAlarmReceiver broadcastReceiver;
    private static delayed_messages inst;


    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
    public static delayed_messages instance() {
        return inst;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.delayed_messages_list_fragment, container, false);
        db=new database_operations(getActivity());

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        add_message=mToolbar.findViewById(R.id.add_message);


        data_recyclerView = view.findViewById(R.id.delayed_message_recycler);
        data_adapter = new delayed_messages_list_adapter(getActivity(), contact_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new delayed_messages_RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(data_recyclerView);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;

            }


            @Override

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                // Row is swiped from recycler view

                // remove it from adapter
            }


            @Override

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(data_recyclerView);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), data_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position)
            {
                show_update_dialog(position);

            }
        }));


        get_messages();


        add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });


        return view;
    }
    public void get_messages(){
        contact_list.clear();
        contact_list.addAll(db.getAll_notification_model());
        data_adapter.notifyDataSetChanged();
    }
    private long insert_data(String snippet,String strAddress,long dateText,String contact_image,String contact_name){
        return new database_operations(getActivity()).insert_delayed_messages_model(snippet,strAddress,dateText,contact_image,contact_name);
    }
    private void show_dialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.delayed_messages_dialog);
        dialog.setTitle("Title...");
        final EditText smstext=(EditText)dialog.findViewById(R.id.sms_message);
        Button confirm=(Button)dialog.findViewById(R.id.confirm);
        Button discard=(Button)dialog.findViewById(R.id.discard);
        final SingleDateAndTimePicker singleDateAndTimePicker = (SingleDateAndTimePicker) dialog.findViewById(R.id.single_day_picker);
        singleDateAndTimePicker.setStepMinutes(1);
        singleDateAndTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                Toast.makeText(getActivity(),displayed,Toast.LENGTH_LONG).show();
            }
        });
        final AutoCompleteTextView mTxtPhoneNo=(AutoCompleteTextView) dialog.findViewById(R.id.sms_number);
        final ArrayList<autocomplete_contact_model>list=get_all_contacts();
        autocomplete_adapter adapter=new autocomplete_adapter(getActivity(),list);
        mTxtPhoneNo.setAdapter(adapter);
        final autocomplete_contact_model[] data = new autocomplete_contact_model[1];
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data[0] =new autocomplete_contact_model(list.get(position).contact_name,
                        list.get(position).contact_phoneNumber,list.get(position).contact_id,list.get(position).contact_image);
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date=singleDateAndTimePicker.getDate();
                String name = "";
                String number =mTxtPhoneNo.getText().toString().replaceAll("\\s+","");
                String image = "no image";
                if (data[0]!=null) {
                    name = data[0].contact_name;
                    number = data[0].contact_phoneNumber.replaceAll("\\s+","");
                    image = data[0].contact_image;
                }
                String sms=smstext.getText().toString();
                Log.w(";sdjaksd",number+" "+sms+" "+name);
                if (!number.isEmpty()&&sms.length()>0&&number.length()<14){
                    long id=insert_data(sms,number,date.getTime(),image,name);
                    set_time(number,sms, (int) id,date);
                    get_messages();
                    dialog.dismiss();
                }else {
                    Toast.makeText(getActivity(),"message isn't correct",Toast.LENGTH_LONG).show();
                }

            }
        });
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void set_time(String smsNumber,String smsText,int id,Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Intent alarmIntent = new Intent(getActivity(), CustomAlarmReceiver.class);

        //pass extra data to CustomAlarmReceiver intent to be handled when the alarm goes off
        alarmIntent.putExtra("alarm_message", smsText);
        alarmIntent.putExtra("number", smsNumber);
        alarmIntent.putExtra("id", id);
        alarmIntent.putExtra("delayed", true);

        // creates a new PendingIntent using the static variable eventID.
        // using eventID allows you to create multiple events with the same code
        // without a unique id the intent would just be updated with new extras each time its created
        //
        PendingIntent pendingAlarm = PendingIntent.getBroadcast(
                getActivity(), id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingAlarm);


    }
    private ArrayList<autocomplete_contact_model> get_all_contacts(){
        ArrayList<autocomplete_contact_model>list=new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor phones = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        while (phones.moveToNext()) {

            String contact_name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contact_phoneNumber = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            long contact_id = phones
                    .getLong(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String contact_image = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

            list.add(new autocomplete_contact_model(contact_name,contact_phoneNumber,contact_id,contact_image));


        }
        phones.close();
        return list;
    }
    private void delete_data(int id){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getActivity(), CustomAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(), id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
         new database_operations(getActivity()).delete_delayed_messages(id);

    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof delayed_messages_list_adapter.MyViewHolder) {
            delete_data(contact_list.get(position).id);
            contact_list.remove(contact_list.get(position));
            data_adapter.notifyDataSetChanged();

        }
    }
    private void show_update_dialog(final int position) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.delayed_messages_dialog);
        dialog.setTitle("Title...");
        final EditText smstext=(EditText)dialog.findViewById(R.id.sms_message);
        Button confirm=(Button)dialog.findViewById(R.id.confirm);
        Button discard=(Button)dialog.findViewById(R.id.discard);
        final SingleDateAndTimePicker singleDateAndTimePicker = (SingleDateAndTimePicker) dialog.findViewById(R.id.single_day_picker);
        singleDateAndTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                Toast.makeText(getActivity(),displayed,Toast.LENGTH_LONG).show();
            }
        });
        final AutoCompleteTextView mTxtPhoneNo=(AutoCompleteTextView) dialog.findViewById(R.id.sms_number);
        final ArrayList<autocomplete_contact_model>list=get_all_contacts();
        autocomplete_adapter adapter=new autocomplete_adapter(getActivity(),list);
        mTxtPhoneNo.setAdapter(adapter);
        final autocomplete_contact_model[] data = new autocomplete_contact_model[1];
        data[0] =new autocomplete_contact_model(contact_list.get(position).contact_name,
                contact_list.get(position).strAddress,contact_list.get(position).id,contact_list.get(position).contact_image);
        if (!contact_list.get(position).contact_name.isEmpty()){
            mTxtPhoneNo.setText(contact_list.get(position).contact_name);
        }else {
            mTxtPhoneNo.setText(contact_list.get(position).strAddress);

        }
        smstext.setText(contact_list.get(position).snippet);

        singleDateAndTimePicker.setDefaultDate(new Date(contact_list.get(position).dateText));
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data[0] =new autocomplete_contact_model(list.get(position).contact_name,
                        list.get(position).contact_phoneNumber,list.get(position).contact_id,list.get(position).contact_image);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date=singleDateAndTimePicker.getDate();
                String name = "";
                String number =mTxtPhoneNo.getText().toString().replaceAll("\\s+","");
                String image = "no image";
                int message_id=contact_list.get(position).id;
                if (data[0]!=null) {
                    name = data[0].contact_name;
                    number = data[0].contact_phoneNumber.replaceAll("\\s+","");
                    image = data[0].contact_image;
                }
                String sms=smstext.getText().toString();
                if (!number.isEmpty()&&sms.length()>0&&number.length()<14){

                    update_data(new delayed_messages_model( message_id,sms,number,date.getTime(),image,name));
                    get_messages();

                    dialog.dismiss();
                }else {
                    Toast.makeText(getActivity(),"message isn't correct",Toast.LENGTH_LONG).show();
                }

            }
        });
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private long update_data(delayed_messages_model hold_message){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getActivity(), CustomAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(), hold_message.id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        set_time(hold_message.strAddress,hold_message.snippet,hold_message.id,new Date(hold_message.dateText));
        return new database_operations(getActivity()).update_delayed_message(hold_message);
    }



}

