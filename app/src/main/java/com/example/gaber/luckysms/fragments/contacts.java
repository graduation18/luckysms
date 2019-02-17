package com.example.gaber.luckysms.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.luckysms.R;
import com.example.gaber.luckysms.activities.messages;
import com.example.gaber.luckysms.adapters.contacts_list_adapter;
import com.example.gaber.luckysms.custom.MyDividerItemDecoration;
import com.example.gaber.luckysms.custom.RecyclerTouchListener;
import com.example.gaber.luckysms.model.contact_model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class contacts extends Fragment  {
    View view;
    private List<contact_model> contact_list = new ArrayList<>();
    private ArrayList<contact_model> filteredList = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private contacts_list_adapter data_adapter;
    private Toolbar mToolbar;
    private EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        view = inflater.inflate(R.layout.contact_list_fragment, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);
        search=mToolbar.findViewById(R.id.search_edt);

        data_recyclerView = view.findViewById(R.id.contacts_recycler);
        data_adapter = new contacts_list_adapter(getActivity(), contact_list);
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
                    goto_chat.putExtra("contact_phoneNumber", contact_list.get(position).contact_phoneNumber);
                    goto_chat.putExtra("contact_image", contact_list.get(position).contact_image);


                    startActivity(goto_chat);
                }else {
                    Intent goto_chat = new Intent(getActivity(), messages.class);

                    goto_chat.putExtra("contact_name", filteredList.get(position).contact_name);
                    goto_chat.putExtra("contact_phoneNumber", filteredList.get(position).contact_phoneNumber);
                    goto_chat.putExtra("contact_image", filteredList.get(position).contact_image);


                    startActivity(goto_chat);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        check_contacts_permission();

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


        return view;
    }

    private void filter(String text) {
        filteredList.clear();
        for (contact_model item : contact_list) {
            if (!item.contact_phoneNumber.isEmpty()){
                if (item.contact_name.toLowerCase().contains(text.toLowerCase())|| item.contact_phoneNumber.contains(text)) {
                    filteredList.add(item);
                }
            }else {
                if (item.contact_name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

        }

        data_adapter.filterList(filteredList);
    }
    private void get_all_contacts(){
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


            contact_list.add(new contact_model(contact_name,contact_phoneNumber,contact_id,contact_image));


        }
        phones.close();
        data_adapter.notifyDataSetChanged();

    }
    private void check_contacts_permission(){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},00);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            get_all_contacts();
        }
    }
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
                    get_all_contacts();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity()," we cannot read your contacts if you didn't allow us to",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                get_all_contacts();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
