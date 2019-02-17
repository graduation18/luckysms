package com.example.gaber.luckysms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gaber.luckysms.model.delayed_messages_model;
import com.example.gaber.luckysms.model.hold_messages_model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gaber on 21/07/2018.
 */

public class database_operations extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "agent_database";

    Context context;


    public database_operations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create  table
        db.execSQL(delayed_messages_model.CREATE_TABLE);
        db.execSQL(hold_messages_model.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + delayed_messages_model.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + hold_messages_model.TABLE_NAME);


        // Create tables again
        onCreate(db);
    }


    //delayed_messages_model
    public long insert_delayed_messages_model(String snippet, String strAddress, long dateText, String contact_image, String contact_name)
    {
        database_operations mDbHelper = new database_operations(context);
        // get writable database as we want to write data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(delayed_messages_model.snippet_sql, snippet);
        values.put(delayed_messages_model.strAddress_sql, strAddress);
        values.put(delayed_messages_model.dateText_sql, dateText);
        values.put(delayed_messages_model.contact_image_sql, contact_image);
        values.put(delayed_messages_model.contact_name_sql, contact_name);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(delayed_messages_model.TABLE_NAME, null, values);
        return newRowId;

        // return newly inserted row id

    }
    public List<delayed_messages_model> getAll_notification_model()
    {
        List<delayed_messages_model> data_modelList = new ArrayList<>();
        // Select All Query
        String countQuery = "SELECT  * FROM " + delayed_messages_model.TABLE_NAME+" ORDER BY "+delayed_messages_model.dateText_sql+" ASC";



        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                delayed_messages_model data = new delayed_messages_model();
                data.snippet=cursor.getString(cursor.getColumnIndex(delayed_messages_model.snippet_sql));
                data.id=cursor.getInt(cursor.getColumnIndex(delayed_messages_model.id_sql));
                data.strAddress=cursor.getString(cursor.getColumnIndex(delayed_messages_model.strAddress_sql));
                data.dateText=cursor.getLong(cursor.getColumnIndex(delayed_messages_model.dateText_sql));
                data.contact_image=cursor.getString(cursor.getColumnIndex(delayed_messages_model.contact_image_sql));
                data.contact_name=cursor.getString(cursor.getColumnIndex(delayed_messages_model.contact_name_sql));
                data_modelList.add(data);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return data_modelList;
    }

    public void delete_delayed_messages(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(delayed_messages_model.TABLE_NAME, delayed_messages_model.id_sql + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public int update_delayed_message(delayed_messages_model delayed_message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(delayed_messages_model.snippet_sql,delayed_message.snippet);
        values.put(delayed_messages_model.strAddress_sql,delayed_message.strAddress);
        values.put(delayed_messages_model.contact_image_sql, delayed_message.contact_image);
        values.put(delayed_messages_model.contact_name_sql, delayed_message.contact_name);
        values.put(delayed_messages_model.dateText_sql, delayed_message.dateText);
        // updating row
        return db.update(delayed_messages_model.TABLE_NAME, values, delayed_messages_model.id_sql + " = ?",
                new String[]{String.valueOf(delayed_message.id)});
    }
    public int get_delayed_messages_Count()
    {

        String countQuery = "SELECT  * FROM " + delayed_messages_model.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);


        int count = cursor.getCount();

        cursor.close();



        // return count

        return count;
    }

    //hold_messages_model
    public long insert_hold_messages_model(String snippet, String strAddress, String contact_image, String contact_name)
    {
        database_operations mDbHelper = new database_operations(context);
        // get writable database as we want to write data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase db2 = mDbHelper.getReadableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(hold_messages_model.snippet_sql, snippet);
        values.put(hold_messages_model.strAddress_sql, strAddress);
        values.put(hold_messages_model.contact_image_sql, contact_image);
        values.put(hold_messages_model.contact_name_sql, contact_name);
// Insert the new row, returning the primary key value of the new row

            return db.insert(hold_messages_model.TABLE_NAME, null, values);





        // return newly inserted row id

    }
    public List<hold_messages_model> getAll_hold_messages()
    {
        List<hold_messages_model> data_modelList = new ArrayList<>();
        // Select All Query
        String countQuery = "SELECT  * FROM " + hold_messages_model.TABLE_NAME;



        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                hold_messages_model data = new hold_messages_model();
                data.snippet=cursor.getString(cursor.getColumnIndex(hold_messages_model.snippet_sql));
                data.strAddress=cursor.getString(cursor.getColumnIndex(hold_messages_model.strAddress_sql));
                data.contact_image=cursor.getString(cursor.getColumnIndex(hold_messages_model.contact_image_sql));
                data.contact_name=cursor.getString(cursor.getColumnIndex(hold_messages_model.contact_name_sql));
                data.id=cursor.getInt(cursor.getColumnIndex(hold_messages_model.id_sql));

                data_modelList.add(data);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return data_modelList;
    }
    public int get_hold_messages_Count()
    {

        String countQuery = "SELECT  * FROM " + hold_messages_model.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);


        int count = cursor.getCount();

        cursor.close();



        // return count

        return count;
    }
    public void delete_hold_message(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(hold_messages_model.TABLE_NAME, hold_messages_model.id_sql + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
    public int update_hold_message(hold_messages_model hold_messages)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(hold_messages_model.snippet_sql,hold_messages.snippet);
        values.put(hold_messages_model.strAddress_sql,hold_messages.strAddress);
        values.put(hold_messages_model.contact_image_sql, hold_messages.contact_image);
        values.put(hold_messages_model.contact_name_sql, hold_messages.contact_name);
        // updating row
        return db.update(hold_messages_model.TABLE_NAME, values, hold_messages_model.id_sql + " = ?",
                new String[]{String.valueOf(hold_messages.id)});
    }


}
