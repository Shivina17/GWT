package com.mycompany.mywebapp.shared;

import java.io.Serializable;

public class Narrator extends Contributor implements Serializable {


    public Narrator() {
        super();
    }

    public Narrator(String cont_name, String cont_desc, String cont_DOB) {
        super(cont_name, cont_desc, cont_DOB);
    }
}
