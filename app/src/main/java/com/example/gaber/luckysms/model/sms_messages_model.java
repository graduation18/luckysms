package com.example.gaber.luckysms.model;

public class sms_messages_model  {
    public String address;
    public long date;
    public String body;
    public int int_Type;
    public String strseen;
    public long date_sent;
    public String strthread_id;
    public long id;
    public int status;
    public sms_messages_model(long id,String address, long date, String body,int int_Type,String strseen,long date_sent,String strthread_id,int status){
        this.address=address;
        this.date=date;
        this.body=body;
        this.int_Type=int_Type;
        this.strseen=strseen;
        this.date_sent=date_sent;
        this.strthread_id=strthread_id;
        this.id=id;
        this.status=status;

    }
}
