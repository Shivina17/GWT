package com.mycompany.mywebapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {

    String ID;
    String DOB;

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return DOB;
    }

    public String getEmail() {
        return email;
    }

    public String[] getPhones() {
        return phones;
    }

    public String getPassword() {
        return password;
    }

    public Customer() {
    }

    String name;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhones(String[] phones) {
        this.phones = phones;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String email;
    String phones[];

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String address;

    public ArrayList<Audiobook> getAudio_ISBNs() {
        return audio_ISBNs;
    }

    public void setAudio_ISBNs(ArrayList<Audiobook> audio_ISBNs) {
        this.audio_ISBNs = audio_ISBNs;
    }

    ArrayList<Audiobook> audio_ISBNs;

    public Customer(String ID, String DOB, String name, String email, String[] phones, String password, String address, ArrayList<Audiobook> audio_ISBNs) {
        this.ID = ID;
        this.DOB = DOB;
        this.name = name;
        this.email = email;
        this.phones = phones;
        this.password = password;
        this.audio_ISBNs = audio_ISBNs;
        this.address = address;
    }

    String password;


}
