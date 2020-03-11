package com.example.mechachromemobileapp;

import java.util.Date;

public class Review {
    private String author, content, book_title;
    private Date date_published;
    private float rating;

    public Review() {

    }

    public Review(String author, Date date_published, String content, float rating){
        this.author = author;
        this.date_published = date_published;
        this.content = content;
        this.rating = rating;
    }

    public Review(String author, String book_title, Date date_published, String content, float rating){
        this.author = author;
        this.book_title = book_title;
        this.date_published = date_published;
        this.content = content;
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

    public void serRating(float score) {
        this.rating = score;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_title() {
        return book_title;
    }
}
