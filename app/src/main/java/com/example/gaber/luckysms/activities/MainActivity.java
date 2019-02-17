package com.example.gaber.luckysms.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.adapters.ViewPagerAdapter_with_titles;
import com.example.gaber.luckysms.custom.AppStatus;
import com.example.gaber.luckysms.fragments.contacts;
import com.example.gaber.luckysms.fragments.conversations;
import com.example.gaber.luckysms.fragments.delayed_messages;
import com.example.gaber.luckysms.fragments.hold_messages;
import com.example.gaber.luckysms.fragments.settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        if (!AppStatus.getInstance(getApplicationContext()).isNetworkAvailable()){
            Toast.makeText(MainActivity.this,"you have no internet conection lucjy sms is down",Toast.LENGTH_LONG).show();
            finish();
        }
        check_user();

        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                                  Intent intent =
                                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                    myPackageName);
                            startActivity(intent);

        }


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.contacts:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.conversations:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.delayed_messages:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.hold_messages:
                                viewPager.setCurrentItem(3);
                                break;
                            case R.id.settings:
                                viewPager.setCurrentItem(4);
                                break;

                        }
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       /*   //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        */
        setupViewPager(viewPager);


    }
    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter_with_titles adapter = new ViewPagerAdapter_with_titles(getSupportFragmentManager());
        contacts contacts_fragment =new contacts();
        conversations conversations_fragment =new conversations();
        delayed_messages delayed_messages_fragment =new delayed_messages();
        hold_messages hold_messages_fragment =new hold_messages();
        settings settings_fragment =new settings();
        adapter.addFragment(contacts_fragment);
        adapter.addFragment(conversations_fragment);
        adapter.addFragment(delayed_messages_fragment);
        adapter.addFragment(hold_messages_fragment);
        adapter.addFragment(settings_fragment);
        viewPager.setAdapter(adapter);
    }
    private void check_user()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean found=false;
                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        if (sub_type.child("contact_phoneNumber").getValue(String .class).contains(getSharedPreferences("logged_in",MODE_PRIVATE).getString("phone","").substring(3))){
                            if (!sub_type.hasChild("device_id")){
                                sub_type.getRef().child("device_id").setValue(Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID));
                            }

                            if (sub_type.child("device_id").getValue(String .class).equals(Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ANDROID_ID))) {
                                found = true;
                            }



                        }
                    }
                    if (found){

                    }else {
                        Toast.makeText(getApplicationContext(),"you'r not allowed to use this application please call the manager",Toast.LENGTH_LONG).show();
                        getSharedPreferences("logged_in",MODE_PRIVATE).edit()
                                .putBoolean("state",false)
                                .putString("phone","")
                                .commit();
                        finish();
                    }

                }else {

                    Toast.makeText(getApplicationContext(),"you'r not allowed to use this application please call the manager",Toast.LENGTH_LONG).show();
                    getSharedPreferences("logged_in",MODE_PRIVATE).edit()
                            .putBoolean("state",false)
                            .putString("phone","")
                            .commit();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("khgj",databaseError.getMessage());


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_user();
        if (!AppStatus.getInstance(getApplicationContext()).isNetworkAvailable()){
            Toast.makeText(MainActivity.this,"you have no internet conection lucjy sms is down",Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
