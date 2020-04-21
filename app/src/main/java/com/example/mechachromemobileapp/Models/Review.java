package com.example.mechachromemobileapp.Models;

import java.util.Date;

public class Review {

    private String author, author_id, content, book_title, book_id;
    private Date date_published;
    private float rating;

    public Review() {
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAuthor(){
        return author;
    }

    public Date getDate_published(){
        return date_published;
    }

    public String getContent(){
        return content;
    }

    public float getRating(){
        return rating;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate_published(Date date_published) {
        this.date_published = date_published;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_title() {
        return book_title;
    }
}
