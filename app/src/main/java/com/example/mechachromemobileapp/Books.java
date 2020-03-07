package com.example.mechachromemobileapp;

public class Books {
    private String title, description, author, category, imgUrl;
    private long numReviews, numRatings,pages;
    private double rating;


    public Books(String title, String author, String description, String category, String imgUrl, long pages, long numReviews, double rating, long numRatings) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.numRatings = numRatings;

    }

    public Books(String title, String author, String category, String imgUrl, long pages, long numReviews, double rating, long numRatings) {
        this.title = title;
        this.author = author;
        this.description = null;
        this.category = category;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.numRatings = numRatings;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public long getNumReviews(){
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public long getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    }