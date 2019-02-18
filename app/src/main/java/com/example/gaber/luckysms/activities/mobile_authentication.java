package com.example.gaber.luckysms.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.custom.AppStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.CountryCodePicker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class mobile_authentication extends AppCompatActivity {


    ProgressBar progress1;
    CountryCodePicker ccp;
    AppCompatEditText edtPhoneNumber;
    CountDownTimer cTimer = null;
     Button request_verify;


    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(getSharedPreferences("logged_in",MODE_PRIVATE).getBoolean("state",false)){
            Intent main=new Intent(this,MainActivity.class);
            startActivity(main);
            finish();
        }
        setContentView(R.layout.activity_mobile_authentication);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        edtPhoneNumber = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        progress1=(ProgressBar)findViewById(R.id.progress1);
        request_verify=(Button)findViewById(R.id.request_verify);




    }


    public void verfiy(View view) {
        String phone="+"+ccp.getSelectedCountryCode()+edtPhoneNumber.getText().toString();
        if (phone.length()==13){
            check_user(edtPhoneNumber.getText().toString(),phone);
        }


    }

    private void check_user(final String sub_phone, final String phone)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean found=false;
                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        if (sub_type.child("contact_phoneNumber").getValue(String .class).contains(sub_phone)){
                            if (!sub_type.hasChild("device_id")){
                                sub_type.getRef().child("device_id").setValue(Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID));
                                found = true;

                            }else {
                                if (sub_type.child("device_id").getValue(String .class).equals(Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID))) {
                                    found = true;
                                }
                            }





                        }
                    }
                    if (found){
                        Intent got_confirm_code=new Intent(mobile_authentication.this,confirm_code.class);
                        Log.w("dsaldj",phone);
                        got_confirm_code.putExtra("phone_number",phone);
                        startActivity(got_confirm_code);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"you'r not allowed to use this application please call the manager",Toast.LENGTH_LONG).show();
                        String token=AppStatus.getInstance(getApplicationContext()).get_manger_token();
                        send_message(phone,token);
                    }

                }else {

                    Toast.makeText(getApplicationContext(),"you'r not allowed to use this application please call the manager",Toast.LENGTH_LONG).show();
                    String token=AppStatus.getInstance(getApplicationContext()).get_manger_token();
                    send_message("+"+ccp.getSelectedCountryCode()+phone,token);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("khgj",databaseError.getMessage());


            }
        });

    }
    private void send_message(String phone,String to_user_token)
    {


        try {
            JSONObject main = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("phone", phone);
            data.put("time",String .valueOf((int)System.currentTimeMillis()));
            main.put("data", data);
            main.put("to", to_user_token);
            String url = "https://fcm.googleapis.com/fcm/send";
            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }
            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, main,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AAAAUsuCQyA:APA91bEuKjef9Hb-LGyy2d_gT_07qyIZLqW6QNigr8G9WpkEe4NhEUCbGHZI5IPzQLEDle9ogaqgmsNODcNcqA5-owae7IHhmSTlkdfyM5hdi-s38_t7f19y0hp_XhHJVcBzcUmWk0m1");

                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {

        }


    }

}


