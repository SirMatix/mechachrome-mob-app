package com.example.mechachromemobileapp;

public class Books {
    private String title, description, author, imgUrl;
    private int pages, review;
    private float rating;
    private int drawableResources;

    public Books() {
    }

    public Books(int drawableResources) {
        this.drawableResources = drawableResources;
    }

    public Books(String title, String description, String author, String imgUrl, int pages, int review, float rating, int drawableResources) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.imgUrl = imgUrl;
        this.pages = pages;
        this.review = review;
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

        return review;
    }

    public void setReview(int review) {

        this.review = review;
    }

    public float getRating() {

        return rating;
    }

    public void setRating(float rating) {

        this.rating = rating;
    }

    public int getDrawableResources() {
        return
                drawableResources;
    }

    public void setDrawableResources(int drawableResources) {

        this.drawableResources = drawableResources;
    }

}