package com.example.gaber.luckysms.model;

/**
 * Created by gaber on 12/08/2018.
 */

public class hold_messages_model {
    public static final String TABLE_NAME = "hold_messages";

    public static final String snippet_sql = "snippet_sql";
    public static final String id_sql = "id_sql";
    public static final String strAddress_sql = "strAddress_sql";
    public static final String contact_image_sql = "contact_image_sql";
    public static final String contact_name_sql = "contact_name_sql";




    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + id_sql + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + snippet_sql + " TEXT,"
                    + strAddress_sql + " TEXT,"
                    + contact_image_sql + " TEXT,"
                    + contact_name_sql + " TEXT"
                    + ")";




    public String  snippet;
    public int id;
    public String strAddress;
    public String contact_image;
    public String contact_name;
    public boolean marked;

    public hold_messages_model(int id,String snippet, String strAddress, String contact_image, String contact_name,boolean marked){
        this.id=id;
        this.snippet=snippet;
        this.strAddress=strAddress;
        this.contact_image=contact_image;
        this.contact_name=contact_name;
        this.marked=marked;
    }
    public hold_messages_model() {

    }

}
