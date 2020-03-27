package com.example.mechachromemobileapp.Models;

public class SaleBook extends Books {

    String seller_id;
    float price;

    public SaleBook() {

    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
