package com.mycompany.mywebapp.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mycompany.mywebapp.client.AddToDatabases;
import com.mycompany.mywebapp.shared.Audiobook;
import com.mycompany.mywebapp.shared.Customer;

import java.sql.SQLException;

public class AddToDatabasesImpl extends RemoteServiceServlet implements AddToDatabases {


    @Override
    public Customer addCustomer(String name, String email, String password, String address) {
        try {
              Customer newCustomer = MainConnect.addCustomer(name, email, password, address);
              return newCustomer;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public Customer addToCart(Customer customer, Audiobook audiobook) {
        try {
            Customer updatedCustomer = MainConnect.addToCart(customer, audiobook);
            return updatedCustomer;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer updateAddressOfCustomer(Customer customer, String address) {
        try {
            System.out.println("GETTING CUSTOMER "+customer.getName());

            Customer updatedCustomer = MainConnect.updateCustomerAddress(customer, address);
            return updatedCustomer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public Customer addPurchase(Customer customer) {
        try {
            Customer updatedCustomer = MainConnect.purchaseBooks(customer);
            return updatedCustomer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void addContributor(String name, String bio, String DOB) {
        try {
            MainConnect.addContributor(name, bio, DOB);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}



/*
 @Override
    public void addCustomer(String name, String email, String password, String address) {

    }


    @Override
    public Audiobook[] addToCart(Customer customer, Audiobook audiobook) {
        Audiobook oldCart[] = customer.getCart();
        int size = oldCart.length;
        Audiobook newCart[] = new Audiobook[size + 1];
        for (int i = 0; i < size; i++) {
            newCart[i] = oldCart[i];
        }
        newCart[size] = audiobook;
        System.out.println(customer.getCart().length);
        customer.setCart(newCart);
        System.out.println(customer.getCart().length);

        return newCart;
    }
}
 */