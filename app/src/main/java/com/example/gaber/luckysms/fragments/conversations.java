package com.example.gaber.luckysms.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.activities.messages;
import com.example.gaber.luckysms.adapters.autocomplete_adapter;
import com.example.gaber.luckysms.adapters.conversations_list_adapter;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.model.autocomplete_contact_model;
import com.example.gaber.luckysms.model.sms_conversation_model;
import com.example.gaber.luckysms.model.sms_messages_model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class conversations extends Fragment  {
    View view;
    private List<sms_conversation_model> contact_list = new ArrayList<>();
    private ArrayList<sms_conversation_model> filteredList = new ArrayList<>();

    private RecyclerView data_recyclerView;
    private conversations_list_adapter data_adapter;
    private Toolbar mToolbar;
    private EditText search;
    private TextView add_message;
    private static conversations inst;


    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
    public static conversations instance() {
        return inst;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.conversation_list_fragment, container, false);


        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        search=mToolbar.findViewById(R.id.search_edt);
        add_message=mToolbar.findViewById(R.id.add_message);


        data_recyclerView = view.findViewById(R.id.conversation_recycler);
        data_adapter = new conversations_list_adapter(getActivity(), contact_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), data_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (search.getText().toString().length()==0) {
                    Intent goto_chat = new Intent(getActivity(), messages.class);

                    goto_chat.putExtra("contact_name", contact_list.get(position).contact_name);
                    goto_chat.putExtra("contact_phoneNumber", contact_list.get(position).strAddress);
                    goto_chat.putExtra("contact_image", contact_list.get(position).contact_image);


                    startActivity(goto_chat);
                }else {
                    Intent goto_chat = new Intent(getActivity(), messages.class);

                    goto_chat.putExtra("contact_name", filteredList.get(position).contact_name);
                    goto_chat.putExtra("contact_phoneNumber", filteredList.get(position).strAddress);
                    goto_chat.putExtra("contact_image", filteredList.get(position).contact_image);


                    startActivity(goto_chat);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        check_messages_permission();


        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                    filter(s.toString());


            }
        });

        add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });

        return view;
    }

    private void filter(String text) {
        filteredList.clear();
        for (sms_conversation_model item : contact_list) {

            if (item.contact_name.toLowerCase().contains(text.toLowerCase())|| item.strAddress.contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        data_adapter.filterList(filteredList);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getEveryLastMessages(){
        contact_list.clear();
        ContentResolver cr = getActivity().getContentResolver();
        String[] SMS_CONVERSATIONS_PROJECTION = new String[]{Telephony.Sms.Conversations.MESSAGE_COUNT,
                Telephony.Sms.Conversations.SNIPPET,Telephony.Sms.Conversations.THREAD_ID};
        Cursor cursor = cr.query(Telephony.Sms.Conversations.CONTENT_URI,
                SMS_CONVERSATIONS_PROJECTION, null, null,Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);
        while(cursor.moveToNext()) {
            int msg_count = cursor.getInt(cursor.getColumnIndex("msg_count"));

            String snippet = cursor.getString(cursor.getColumnIndex("snippet"));

            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));

            Uri convoUri = Telephony.Sms.Conversations.CONTENT_URI
                    .buildUpon().appendPath(thread_id).build();
            String[] projection = new String[] {"address", "date" ,"read" };
            Cursor cursor2 = cr.query(convoUri,
                    projection, null, null,Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);

            int seen_count=0;
            while (cursor2.moveToNext()){
                int index_seen = cursor2.getColumnIndex("read");
                String  boolean_seen = cursor2.getString(index_seen);
                if (boolean_seen.contains("0")){
                    seen_count++;
                }
            }
            cursor2.moveToLast();
            int index_Address = cursor2.getColumnIndex("address");
            int index_Date = cursor2.getColumnIndex("date");
            int index_seen = cursor2.getColumnIndex("read");

            String strAddress = cursor2.getString(index_Address);
            long longDate = cursor2.getLong(index_Date);
            String  boolean_seen = cursor2.getString(index_seen);
            Date date=new Date(longDate);
            SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yy hh:mm:ss a");
            String dateText = df2.format(date);

            cursor2.close();

            Cursor phones = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '" + strAddress + "' OR "+ContactsContract.CommonDataKinds.Phone.TYPE_WORK+" LIKE '" + strAddress + "'",
                    null, null);
            String contact_image=null;
            String contact_name="";
            if (phones.moveToNext()) {
                contact_image = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                contact_name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            }
            phones.close();

            contact_list.add(new sms_conversation_model(msg_count,snippet,thread_id,strAddress,dateText,boolean_seen,contact_image,contact_name,seen_count));
        }
        cursor.close();
        data_adapter.notifyDataSetChanged();

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void check_messages_permission(){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_SMS},00);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            getEveryLastMessages();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 00: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getEveryLastMessages();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity()," we cannot read your contacts if you didn't allow us to",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                getEveryLastMessages();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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
                if (!number.isEmpty()&&sms.length()>0&&number.length()<14){
                    sendSMS(number,sms);
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
                insert_sms_to_sent(phoneNo,msg);

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

    public void insert_sms_to_sent(String phone,String sms){
         ContentValues values = new ContentValues();
        values.put("address",phone);
        values.put("body", sms);
        values.put("seen", true);
        values.put("read", true);
        values.put("status", 1);
        values.put("date",System.currentTimeMillis());
        getActivity().getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getEveryLastMessages();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getEveryLastMessages();
        }
    }
}
