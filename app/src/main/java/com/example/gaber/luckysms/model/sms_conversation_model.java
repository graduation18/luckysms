package com.example.gaber.luckysms.model;

public class sms_conversation_model {
    public int msg_count;
    public String  snippet;
    public String thread_id;
    public String strAddress;
    public String dateText;
    public String boolean_seen;
    public String contact_image;
    public String contact_name;
    public int number_of_nonseen;

    public sms_conversation_model(int msg_count, String snippet, String thread_id,
                                  String strAddress,String dateText,String boolean_seen,String contact_image
            ,String contact_name,int number_of_nonseen){
        this.msg_count=msg_count;
        this.snippet=snippet;
        this.thread_id=thread_id;
        this.strAddress=strAddress;
        this.dateText=dateText;
        this.boolean_seen=boolean_seen;
        this.contact_image=contact_image;
        this.contact_name=contact_name;
        this.number_of_nonseen=number_of_nonseen;
    }
}
