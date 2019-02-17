package com.example.gaber.luckysms.model;

public class autocomplete_contact_model {
    public String contact_name;
    public String contact_phoneNumber;
    public long contact_id;
    public String contact_image;
    public autocomplete_contact_model(String contact_name, String contact_phoneNumber
            , long contact_id, String contact_image){
        this.contact_name=contact_name;
        this.contact_phoneNumber=contact_phoneNumber;
        this.contact_id=contact_id;
        this.contact_image=contact_image;
    }

}
