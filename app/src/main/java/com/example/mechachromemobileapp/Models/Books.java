package com.example.mechachromemobileapp.Models;

import java.util.Date;

public class Books {
    private String title, description, author, category, imgUrl;
    private Integer numReviews, pages, totalBooksNum, availableBooksNum, numReserved;
    private Date add_date;
    private Float rating;

    public Books(){

    }


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

    public Date getAdd_date() {
        return add_date;
    }

    public void setAdd_date(Date add_date) {
        this.add_date = add_date;
    }

    public Integer getTotalBooksNum() {
        return totalBooksNum;
    }

    public void setTotalBooksNum(Integer totalBooksNum) {
        this.totalBooksNum = totalBooksNum;
    }
}