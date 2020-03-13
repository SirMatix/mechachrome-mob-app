package com.example.mechachromemobileapp.Models;

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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getStudentID(){
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
