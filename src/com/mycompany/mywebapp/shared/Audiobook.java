package com.mycompany.mywebapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Audiobook implements Serializable {

    String audioTitle;
    String isbn;
    String fileLoc;
    String[] genres;
    String price;
    String pubDate;
    String ageRate;

    ArrayList<Author> authors;
    Narrator narrator;
    String pub_ID;

    public String getAudioTitle() {
        return audioTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getFileLoc() {
        return fileLoc;
    }

    public String[] getGenres() {
        return genres;
    }

    public String getPrice() {
        return price;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getAgeRate() {
        return ageRate;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public Narrator getNarrator() {
        return narrator;
    }

    public String getPub_ID() {
        return pub_ID;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    ArrayList<Review> reviews;

    public String getAudio_image(){
        return audio_image;
    }

    String audio_image;

    public Audiobook() {
    }

    public Audiobook(String isbn, String fileLoc, String pubDate, String price, String ageRate, String title,
                     String pub_ID, String audio_image, String[] genres, ArrayList<Author> authors, Narrator narrator, ArrayList<Review> reviews) {
        this.isbn = isbn;
        this.fileLoc = fileLoc;
        this.pubDate = pubDate;
        this.price = price;
        this.ageRate = ageRate;
        this.audioTitle = title;
        this.pub_ID = pub_ID;
        this.genres = genres;
        this.audio_image = audio_image;
        this.authors = authors;
        this.narrator = narrator;
        this.reviews = reviews;
    }


}
