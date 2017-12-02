package com.mycompany.mywebapp.shared;


import java.io.Serializable;

public class Contributor implements Serializable {

    String cont_name;

    public Contributor() {

    }

    public Contributor(String cont_name, String cont_desc, String cont_DOB) {
        this.cont_name = cont_name;
        this.cont_desc = cont_desc;
        this.cont_DOB = cont_DOB;
    }

    String cont_desc;

    public String getCont_DOB() {
        return cont_DOB;
    }

    public String getCont_desc() {
        return cont_desc;
    }

    public String getCont_name() {
        return cont_name;
    }

    String cont_DOB;
}
