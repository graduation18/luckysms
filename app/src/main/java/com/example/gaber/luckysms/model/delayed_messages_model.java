package com.example.gaber.luckysms.model;

/**
 * Created by gaber on 12/08/2018.
 */

public class delayed_messages_model {
    public static final String TABLE_NAME = "delayed_messages";

    public static final String snippet_sql = "snippet_sql";
    public static final String id_sql = "id_sql";
    public static final String strAddress_sql = "strAddress_sql";
    public static final String dateText_sql = "dateText_sql";
    public static final String contact_image_sql = "contact_image_sql";
    public static final String contact_name_sql = "contact_name_sql";




    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + id_sql + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + snippet_sql + " TEXT,"
                    + strAddress_sql + " TEXT,"
                    + dateText_sql + " INTEGER,"
                    + contact_image_sql + " TEXT,"
                    + contact_name_sql + " TEXT"
                    + ")";




    public String  snippet;
    public int id;
    public String strAddress;
    public long dateText;
    public String contact_image;
    public String contact_name;

    public delayed_messages_model(int id,String snippet, String strAddress, long dateText, String contact_image, String contact_name){
        this.id=id;
        this.snippet=snippet;
        this.strAddress=strAddress;
        this.dateText=dateText;
        this.contact_image=contact_image;
        this.contact_name=contact_name;
    }
    public delayed_messages_model() {

    }

}
