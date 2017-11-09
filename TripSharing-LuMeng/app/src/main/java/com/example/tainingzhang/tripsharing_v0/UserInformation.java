package com.example.tainingzhang.tripsharing_v0;

/**
 * Created by wanqingchen on 11/8/17.
 */

public class UserInformation {

    private String name;
    private String contact;


    public UserInformation() {

    }

    public UserInformation(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
