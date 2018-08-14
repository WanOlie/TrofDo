package com.example.user.trofdo;

public class Users {

    private String UName;
    private String UIDno;
    private String UEmail;
    private String UPhone;
    private String UCategory;

    public Users(){

    }

    public Users(String UName, String UIDno, String UEmail, String UPhone, String UCategory) {
        this.UName = UName;
        this.UIDno = UIDno;
        this.UEmail = UEmail;
        this.UPhone = UPhone;
        this.UCategory = UCategory;
    }

    public String getUName() {
        return UName;
    }

    public String getUIDno() {
        return UIDno;
    }

    public String getUEmail() {
        return UEmail;
    }

    public String getUPhone() {
        return UPhone;
    }

    public String getUCategory() {
        return UCategory;
    }
}
