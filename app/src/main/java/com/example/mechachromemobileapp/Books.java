package com.example.mechachromemobileapp;

public class Books {
    private String title, description, author, category, imgUrl;
    private Integer numReviews, pages, availableBooksNum, numReserved;
    private Float rating;

    public Books(){

    }

    /*
    public Books(String title, String author, String description, String category, String imgUrl, Integer availableBooksNum, Integer pages, Integer numReviews, Float rating) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.imgUrl = imgUrl;
        this.availableBooksNum = availableBooksNum;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
    }

     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getAvailableBooksNum() {
        return availableBooksNum;
    }

    public void setAvailableBooksNum(Integer availableBooksNum) {
        this.availableBooksNum = availableBooksNum;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public Integer getNumReviews(){
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Integer getNumReserved() {
        return numReserved;
    }

    public void setNumReserved(Integer numReserved) {
        this.numReserved = numReserved;
    }
}