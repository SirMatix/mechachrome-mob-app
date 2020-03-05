package com.example.mechachromemobileapp;

public class Books {
    private String title, description, author, imgUrl, category;
    private int pages, numReviews, drawableResources, numRatings;
    private float rating;


    public Books(String title, String author, String description, String category, int pages, int numReviews, float rating, int numRatings) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.numRatings = numRatings;
        this.drawableResources = 0;
    }

    public Books(String title, String author, String category, int pages, int numReviews, float rating, int numRatings, int drawableResources) {
        this.title = title;
        this.author = author;
        this.description = null;
        this.category = category;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.numRatings = numRatings;
        this.drawableResources = drawableResources;
    }


    public Books(String title, String author, String description, String category, int pages, int numReviews, float rating, int numRatings, int drawableResources) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.numRatings = numRatings;
        this.drawableResources = drawableResources;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public int getNumReviews(){
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public int getNumRatings() {
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDrawableResources() {
        return drawableResources;
    }

    public void setDrawableResources(int drawableResources) {
        this.drawableResources = drawableResources;
    }

    }