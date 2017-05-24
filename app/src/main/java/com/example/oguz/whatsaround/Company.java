package com.example.oguz.whatsaround;

/**
 * Created by oguz on 23.05.2017.
 */

public class Company {
    private String name;
    private String phone;
    private String email;
    private int serviceId;

    public Company() {
    }

    public Company(String name, String phone, String email, int serviceId) {

        this.name = name.replaceAll(" ","@");
        this.phone = phone;
        this.email = email;
        this.serviceId = serviceId;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public int getServiceId() {
        return this.serviceId;
    }

    public String getFormattedName() {return this.name.replaceAll("@"," ");}
}
