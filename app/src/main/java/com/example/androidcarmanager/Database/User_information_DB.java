package com.example.androidcarmanager.Database;

public class User_information_DB {
    String firstname ,lastname, gender, phone;

    public User_information_DB() {
    }

    public User_information_DB(String firstname,String lastname, String gender, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.phone = phone;
    }

    public String getfirstname() {
        return firstname;
    }

    public void setfirstname(String firstname) { this.firstname= firstname; }
    public String getlastname() {
        return lastname;
    }

    public void setlastname(String lastname) { this.lastname = lastname; }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
