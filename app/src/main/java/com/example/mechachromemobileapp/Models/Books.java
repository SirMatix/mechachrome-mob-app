package com.example.mechachromemobileapp.Models;

import java.util.Date;

/**
 *  This class holds all the variables for library books
 */
public class Books {
    private String title, description, author, category, imgUrl, ISBN;
    private Integer numReviews, pages, totalBooksNum, availableBooksNum, numReserved;
    private Date addDate;
    private Float rating;

    public Books(){
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Integer getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Integer numReviews) {
        this.numReviews = numReviews;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getTotalBooksNum() {
        return totalBooksNum;
    }

    public void setTotalBooksNum(Integer totalBooksNum) {
        this.totalBooksNum = totalBooksNum;
    }

    public Integer getAvailableBooksNum() {
        return availableBooksNum;
    }

    public void setAvailableBooksNum(Integer availableBooksNum) {
        this.availableBooksNum = availableBooksNum;
    }

    public Integer getNumReserved() {
        return numReserved;
    }

    public void setNumReserved(Integer numReserved) {
        this.numReserved = numReserved;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}