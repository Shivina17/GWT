package com.mycompany.mywebapp.shared;

import java.io.Serializable;

public class Author extends Contributor implements Serializable {

    public Author() {
    }

    public Author(String cont_name, String cont_desc, String cont_DOB) {
        super(cont_name, cont_desc, cont_DOB);
    }
}




