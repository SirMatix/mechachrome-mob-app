package com.example.mechachromemobileapp;

public class Books {
    private String title, description, author, imgUrl;
    private int pages, numReviews, drawableResources;
    private float rating;

    public Books() {
    }

    public Books(int drawableResources) {
        this.drawableResources = drawableResources;
    }

    public Books(String title, String description, String author, String imgUrl, int pages, int numReviews, float rating, int drawableResources) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.drawableResources = drawableResources;
    }

    public Books(String title, String author, String imgUrl, int pages, int numReviews, float rating, int drawableResources) {
        this.title = title;
        this.author = author;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.drawableResources = drawableResources;
    }

    public Books(String title, String author, int pages, int numReviews, float rating, int drawableResources) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.numReviews = numReviews;
        this.rating = rating;
        this.drawableResources = drawableResources;
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

    public int getReview() {

        return numReviews;
    }

    public void setReview(int review) {

        this.numReviews = numReviews;
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