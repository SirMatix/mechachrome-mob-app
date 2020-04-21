package com.example.mechachromemobileapp.Models;

/**
 * Child class of Books class, holds additional variables for
 * books for sale
 */
public class BookSaleModel extends Books {

    private String seller_id, condition;
    private float price;
    private boolean sold;

    public BookSaleModel() {
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }
}
