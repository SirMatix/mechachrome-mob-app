package com.example.mechachromemobileapp;

public class Review {
    private String author;
    private String date_published;
    private String content;
    private int score;

    public Review() {

    }

    public Review(String author, String date_published, String content, int score){
        this.author = author;
        this.date_published = date_published;
        this.content = content;
        this.score = score;
    }

    public String getAuthor(){
        return author;
    }

    public String getDate_published(){
        return date_published;
    }

    public String getContent(){
        return content;
    }

    public int getScore(){
        return score;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
