package com.mycompany.mywebapp.shared;

import java.io.Serializable;

public class Review implements Serializable {

    public Review() {

    }

    String customer_ID;

    public Review(String comment, String rating, String customer_ID) {
        this.customer_ID = customer_ID;
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getRating() {
        return rating;
    }

    public String getCustomer_ID() {
        return customer_ID;
    }

    String comment;
    String rating;


}
