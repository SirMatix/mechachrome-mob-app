package com.example.mechachromemobileapp.Models;

import java.util.Date;

public class Reservation {

    private String user_reserved_for, user_reserved_id, book_title, book_author, book_id;
    private Date reserved_from, reserved_to;
    private Boolean is_active, is_cancelled, is_done;

    public Reservation(){
    }

    public String getUser_reserved_for() {
        return user_reserved_for;
    }

    public void setUser_reserved_for(String user_reserved_for) {
        this.user_reserved_for = user_reserved_for;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getUser_reserved_id() {
        return user_reserved_id;
    }

    public void setUser_reserved_id(String user_reserved_id) {
        this.user_reserved_id = user_reserved_id;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public Date getReserved_from() {
        return reserved_from;
    }

    public void setReserved_from(Date reserved_from) {
        this.reserved_from = reserved_from;
    }

    public Date getReserved_to() {
        return reserved_to;
    }

    public void setReserved_to(Date reserved_to) {
        this.reserved_to = reserved_to;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Boolean getIs_cancelled() {
        return is_cancelled;
    }

    public void setIs_cancelled(Boolean is_cancelled) {
        this.is_cancelled = is_cancelled;
    }

    public Boolean getIs_done() {
        return is_done;
    }

    public void setIs_done(Boolean is_done) {
        this.is_done = is_done;
    }

}
