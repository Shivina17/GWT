package com.mycompany.mywebapp.shared;

import java.io.Serializable;

public class Employee  implements Serializable{

    String id;
    String name;

    public Employee() {
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    String email;

    public Employee(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    String password;
}
