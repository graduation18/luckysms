package com.example.gaber.luckysms.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.adapters.autocomplete_adapter;
import com.example.gaber.luckysms.adapters.contacts_list_adapter;
import com.example.gaber.luckysms.adapters.messages_list_adapter;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.PicassoCircleTransformation;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.fragments.conversations;
import com.example.gaber.luckysms.model.autocomplete_contact_model;
import com.example.gaber.luckysms.model.contact_model;
import com.example.gaber.luckysms.model.sms_messages_model;
import com.example.gaber.luckysms.services.SMSReceiver;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class messages extends AppCompatActivity {
    private static messages inst;
    private List<sms_messages_model> sms_messages_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private messages_list_adapter data_adapter;
    private EditText message_edit_text;
    private TextView contact_name;
    private ImageView contact_image;
    private String phone,name,image;
    private ProgressWindow progressWindow ;
    private  SMSReceiver broadcastReceiver;

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
    public static messages instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_messages);
        progressConfigurations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        data_recyclerView = findViewById(R.id.contacts_recycler);
        data_adapter = new messages_list_adapter(this, sms_messages_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.setAdapter(data_adapter);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, data_recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(messages.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(messages.this);
                }
                builder.setTitle("DELETE")
                        .setMessage("Sure You Want To Delete This Message?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getContentResolver().delete(
                                        Uri.parse("content://sms/" + sms_messages_list.get(position).id), null, null);
                                refresh();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        }));

        message_edit_text=(EditText)findViewById(R.id.message) ;
        contact_name=(TextView)findViewById(R.id.contact_name) ;
        contact_image=(ImageView)findViewById(R.id.contact_image);

        phone=getIntent().getStringExtra("contact_phoneNumber").replaceAll("\\s+","");
        name=getIntent().getStringExtra("contact_name");
        image=getIntent().getStringExtra("contact_image");

        if (!name.isEmpty()){
            contact_name.setText(name);
        }else {
            contact_name.setText(phone);

        }



            Picasso.with(this)
                    .load(image)
                    .placeholder(R.drawable.nothumbinal_message)
                    .transform(new PicassoCircleTransformation())
                    .into(contact_image, new Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override public void onError() {
                        }
                    });


        check_read_messages_permission();


    }
    private void get_delivered_messages(){

        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type","seen","date_sent","thread_id","status"};
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");
            if (cur.moveToFirst()) {
                int index_id = cur.getColumnIndex("_id");
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                int index_seen = cur.getColumnIndex("seen");
                int index_date_sent = cur.getColumnIndex("date_sent");
                int index_thread_id = cur.getColumnIndex("thread_id");
                int index_status = cur.getColumnIndex("status");

                do {
                    long id = cur.getLong(index_id);
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);
                    String strseen = cur.getString(index_seen);
                    long date_sent = cur.getInt(index_date_sent);
                    String strthread_id = cur.getString(index_thread_id);
                    int int_status = cur.getInt(index_status);

                    if (strAddress.contains(phone.replaceAll("\\s+",""))) {
                        sms_messages_list.add(new sms_messages_model(id,strAddress, longDate, strbody,int_Type,strseen,date_sent,strthread_id,int_status));

                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
                get_sent_messages();
            }

    } catch (SQLiteException ex) {
        Log.d("SQLiteException", ex.getMessage());
    }
}
    private void get_sent_messages(){

        final String SMS_URI_INBOX = "content://sms/sent";
        final String SMS_URI_ALL = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type","seen","date_sent","thread_id","status"};
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");
            if (cur.moveToFirst()) {
                int index_id = cur.getColumnIndex("_id");
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                int index_seen = cur.getColumnIndex("seen");
                int index_date_sent = cur.getColumnIndex("date_sent");
                int index_thread_id = cur.getColumnIndex("thread_id");
                int index_status = cur.getColumnIndex("status");

                do {
                    long id = cur.getLong(index_id);
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);
                    String strseen = cur.getString(index_seen);
                    long date_sent = cur.getInt(index_date_sent);
                    String strthread_id = cur.getString(index_thread_id);
                    int int_status = cur.getInt(index_status);


                    if (strAddress.contains(phone.replaceAll("\\s+",""))) {
                        sms_messages_list.add(new sms_messages_model(id,getSharedPreferences("logged_in",MODE_PRIVATE).getString("phone","00"), longDate, strbody,int_Type,strseen,date_sent,strthread_id,int_status));

                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
                sort_messages(sms_messages_list);
            }
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
    }
    private void sort_messages(List<sms_messages_model>list){
        Collections.sort(list, new Comparator<sms_messages_model>(){
            public int compare(sms_messages_model o1, sms_messages_model o2){
                if(o1.date == o2.date)
                    return 0;
                return o1.date < o2.date ? -1 : 1;
            }
        });
        data_adapter.notifyDataSetChanged();
        if (sms_messages_list.size()>2){
            data_recyclerView.smoothScrollToPosition(sms_messages_list.size() - 1);
        }

    }
    private void check_read_messages_permission(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},00);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            get_delivered_messages();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 01: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    get_delivered_messages();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this," we cannot send your contacts if you didn't allow us to",Toast.LENGTH_LONG).show();
                    this.finish();
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    public void send_message(View view) {
        String message_sms=message_edit_text.getText().toString();
        if (message_sms.length()>0) {
            sendSMS(phone,message_sms);

        }else {
            Toast.makeText(this,"please type message first",Toast.LENGTH_LONG).show();
        }

    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            if (progressWindow==null){
                showProgress();
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(messages.this, "Message Sent",
                            Toast.LENGTH_LONG).show();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                insert_sms_to_sent(phoneNo,msg);
                message_edit_text.setText("");
                refresh();
                if (progressWindow!=null){
                    hideProgress();
                }
            }


        } catch (final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(messages.this,ex.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            });

            ex.printStackTrace();
        }
    }


    public void refresh(){
        sms_messages_list.clear();
        get_delivered_messages();
    }
    public void back(View view) {
        Intent back=new Intent(this,MainActivity.class);
        startActivity(back);
        finish();
    }
    private void progressConfigurations(){
        progressWindow = ProgressWindow.getInstance(this);
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
    protected void onPause() {
        super.onPause();
        hideProgress();

    }
     public void insert_sms_to_sent(String phone,String sms){
         ContentValues values = new ContentValues();
         values.put("address",phone);
         values.put("body", sms);
         values.put("seen", true);
         values.put("read", true);
         values.put("status", 1);
         values.put("date",System.currentTimeMillis());
         getContentResolver().insert(Uri.parse("content://sms/sent"), values);
     }




    public static final String FONT = "assets/gom.ttf";
    public static final String ARABIC = "\u0627\u0644\u0633\u0639\u0631 \u0627\u0644\u0627\u062c\u0645\u0627\u0644\u064a";

    public void write(String fname, String fcontent) throws IOException, DocumentException {
        Document document = new Document();
        String fpath = "/sdcard/" + fname + ".pdf";
        File file = new File(fpath);

        if (!file.exists()) {
            file.createNewFile();
        }
        PdfWriter.getInstance(document,
                new FileOutputStream(file.getAbsoluteFile()));
        document.open();
        Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        PdfPTable table = new PdfPTable(1);
        Phrase phrase = new Phrase();
        Chunk chunk = new Chunk(fcontent);
        phrase.add(chunk);
        phrase.add(new Chunk(fcontent));
        PdfPCell cell = new PdfPCell(phrase);
        cell.setUseDescender(true);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.addCell(cell);
        document.add(table);
        document.close();
    }


    public void save_pdf(View view) {

        show_dialog();
    }
    private void show_dialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pdf_save_dialog);
        dialog.setTitle("Title...");
        final EditText file_name=(EditText)dialog.findViewById(R.id.file_name);
        Button confirm=(Button)dialog.findViewById(R.id.confirm);
        Button discard=(Button)dialog.findViewById(R.id.discard);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_name.getText().toString().length()>0) {
                    String messages=get_messages(sms_messages_list);
                    Log.w("njsadjdsajdsjl",messages);
                    try {
                        write(file_name.getText().toString(),messages);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
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

    private String get_messages(List<sms_messages_model>model){
        StringBuilder  stringBuilder = new StringBuilder();
        for (sms_messages_model message:model){
            Date date=new Date(message.date);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            String dateText = df2.format(date);

            stringBuilder.append(message.address)
                    .append("\n"+message.body)
                    .append("\n"+dateText)
                    .append("\n\n\n\n\n");
        }
        Log.w("njsadjdsajdsjl",stringBuilder.toString());

        return stringBuilder.toString();
    }


}
