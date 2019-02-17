package com.example.gaber.luckysms.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.SmsManager;
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
import com.example.gaber.luckysms.activities.MainActivity;
import com.example.gaber.luckysms.activities.messages;
import com.example.gaber.luckysms.adapters.autocomplete_adapter;
import com.example.gaber.luckysms.adapters.contacts_list_adapter;
import com.example.gaber.luckysms.adapters.hold_messages_list_adapter;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.custom.hold_messages_RecyclerItemTouchHelper;
import com.example.gaber.luckysms.db.database_operations;
import com.example.gaber.luckysms.model.autocomplete_contact_model;
import com.example.gaber.luckysms.model.contact_model;
import com.example.gaber.luckysms.model.hold_messages_model;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class hold_messages extends Fragment implements hold_messages_RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    View view;
    private List<hold_messages_model> contact_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private hold_messages_list_adapter data_adapter;
    private Toolbar mToolbar;
    private TextView add_message;
    private Button send;
    private ProgressWindow progressWindow ;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.hold_messages_list_fragment, container, false);
        progressConfigurations();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        add_message=mToolbar.findViewById(R.id.add_message);

        send=(Button)view.findViewById(R.id.send);
        data_recyclerView = view.findViewById(R.id.hold_messages_recycler);
        data_adapter = new hold_messages_list_adapter(getActivity(), contact_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new hold_messages_RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
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
            public void onLongClick(View view, int position) {
                show_update_dialog(position);

            }
        }));

        getallMessages();

        add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (hold_messages_model message:contact_list){
                    if (message.marked){
                        new LongOperation().execute(message);

                    }
                }
            }
        });
        return view;
    }

    private void getallMessages(){
        contact_list.clear();
        contact_list.addAll(new database_operations(getActivity()).getAll_hold_messages());
        data_adapter.notifyDataSetChanged();
    }
    private void show_dialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.hold_messages_dialog);
        dialog.setTitle("Title...");
        final EditText smstext=(EditText)dialog.findViewById(R.id.sms_message);
        Button confirm=(Button)dialog.findViewById(R.id.confirm);
        Button discard=(Button)dialog.findViewById(R.id.discard);

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
                String name = "";
                String number =mTxtPhoneNo.getText().toString().replaceAll("\\s+","");
                String image = "no image";
                if (data[0]!=null) {
                    name = data[0].contact_name;
                    number = data[0].contact_phoneNumber.replaceAll("\\s+","");
                    image = data[0].contact_image;
                }
                String sms=smstext.getText().toString();
                Log.w("lkdsl",name+" "+number+" "+sms);
                if (!number.isEmpty()&&sms.length()>0&&number.length()<14){
                    long id=insert_data(sms,number,image,name);
                    getallMessages();
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
    private void show_update_dialog(final int position) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.hold_messages_dialog);
        dialog.setTitle("Title...");
        final EditText smstext=(EditText)dialog.findViewById(R.id.sms_message);
        Button confirm=(Button)dialog.findViewById(R.id.confirm);
        Button discard=(Button)dialog.findViewById(R.id.discard);
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
                String name = "";
                String number =mTxtPhoneNo.getText().toString().replaceAll("\\s+","");
                String image = "no image";
                int message_id=contact_list.get(position).id;
                boolean marked=contact_list.get(position).marked;
                if (data[0]!=null) {
                    name = data[0].contact_name;
                    number = data[0].contact_phoneNumber.replaceAll("\\s+","");
                    image = data[0].contact_image;
                }
                String sms=smstext.getText().toString();
                if (!number.isEmpty()&&sms.length()>0&&number.length()<14){

                    update_data(new hold_messages_model( message_id,sms,number,image,name,marked));
                    getallMessages();
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
    private long insert_data(String snippet,String strAddress,String contact_image,String contact_name){
        return new database_operations(getActivity()).insert_hold_messages_model(snippet,strAddress,contact_image,contact_name);
    }
    private long update_data(hold_messages_model hold_message){
        return new database_operations(getActivity()).update_hold_message(hold_message);
    }
    private void delete_data(hold_messages_model hold_message){
        new database_operations(getActivity()).delete_hold_message(hold_message.id);
        getallMessages();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof hold_messages_list_adapter.MyViewHolder) {
            delete_data(contact_list.get(position));
            contact_list.remove(contact_list.get(position));
            data_adapter.notifyDataSetChanged();

        }
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                insert_sms_to_inbox(phoneNo,msg,getActivity(),System.currentTimeMillis(),"inbox");
            }

        } catch (final Exception ex) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(),ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            });

            ex.printStackTrace();
        }
    }


    private void progressConfigurations(){
        progressWindow = ProgressWindow.getInstance(getActivity());
        ProgressWindowConfiguration progressWindowConfiguration = new ProgressWindowConfiguration();
        progressWindowConfiguration.backgroundColor = Color.parseColor("#32000000") ;
        progressWindowConfiguration.progressColor = Color.WHITE ;
        progressWindow.setConfiguration(progressWindowConfiguration);
    }
    public void showProgress(){
        progressWindow.showProgress();
    }
    public void hideProgress(){
        progressWindow.hideProgress();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgress();

    }
    private class LongOperation extends AsyncTask<hold_messages_model, Void, String> {
        hold_messages_model message=new hold_messages_model();
        @Override
        protected String doInBackground(hold_messages_model... params) {
            message=params[0];
           sendSMS(params[0].strAddress,params[0].snippet);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressWindow!=null) {
                hideProgress();
            }
            delete_data(message);

        }

        @Override
        protected void onPreExecute() {
            if (progressWindow==null) {
                showProgress();
            }


        }


    }

    private void insert_sms_to_inbox(String phone, String sms, Context context, long date,String folderName){
        ContentValues values = new ContentValues();
        values.put("address",phone);
        values.put("body", sms);
        values.put("date",date);
        values.put("seen", true);
        values.put("read", true);
        values.put("status", 1);
        values.put("date",date);

        Log.w("kjdsjdsaj","looped");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Uri uri = Telephony.Sms.Sent.CONTENT_URI;
            if(folderName.equals("inbox")){
                uri = Telephony.Sms.Inbox.CONTENT_URI;
            }
            context.getContentResolver().insert(uri, values);
        }
        else {
            context.getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
        }


    }


}
