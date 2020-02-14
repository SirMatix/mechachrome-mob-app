package com.example.mechachromemobileapp;

public class User {

    public String studentID, fname, lname, email;

    public User() {

    }

    public User(String studentID, String fname, String lname, String email) {
        this.studentID = studentID;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

    public String getStudentID(){
        return studentID;
    }
    public String getEmail(){
        return email;
    }

}
