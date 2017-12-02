package com.mycompany.mywebapp.shared;


import java.io.Serializable;

public class Purchase implements Serializable {
    Audiobook[] audiobooks;

    public Purchase(Customer customer, Audiobook[] audiobooks, String date) {
        this.audiobooks = audiobooks;
        this.customer = customer;
        this.date = date;
    }

    Customer customer;
    String date;

    public Purchase() {
    }

    public Audiobook[] getAudiobooks() {
        return audiobooks;
    }

    public void setAudiobooks(Audiobook[] audiobooks) {
        this.audiobooks = audiobooks;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
