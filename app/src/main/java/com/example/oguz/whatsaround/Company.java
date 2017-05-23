package com.example.oguz.whatsaround;

/**
 * Created by oguz on 23.05.2017.
 */

public class Company {
    String name;
    String phone;
    String email;
    int serviceId;
    public Company(){
    }
    public Company(String name,String phone,String email,int serviceId){

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceId = serviceId;
    }
}
