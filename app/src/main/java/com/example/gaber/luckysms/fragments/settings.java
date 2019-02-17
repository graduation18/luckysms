package com.example.gaber.luckysms.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.activities.messages;
import com.example.gaber.luckysms.adapters.contacts_list_adapter;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.model.contact_model;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class settings extends Fragment  {
    View view;
    Button logout;
    Switch mute_notifications;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        logout=(Button)view.findViewById(R.id.logout);
        mute_notifications=(Switch)view.findViewById(R.id.mute_notifications);
        if (getActivity().getSharedPreferences("notifications_mute",MODE_PRIVATE).getBoolean("state",false)){
            mute_notifications.setChecked(true);
        }
        mute_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getActivity().getSharedPreferences("notifications_mute", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", true)
                            .apply();
                }else {
                    getActivity().getSharedPreferences("notifications_mute", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", false)
                            .apply();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSharedPreferences("logged_in",MODE_PRIVATE).edit()
                        .putBoolean("state",false)
                        .commit();
                getActivity().finish();
            }
        });
        return view;
    }



}
