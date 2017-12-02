package com.mycompany.mywebapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.mywebapp.shared.Audiobook;
import com.mycompany.mywebapp.shared.Customer;


public interface AddToDatabasesAsync {
    void addCustomer(String name, String email, String password, String address, AsyncCallback<Customer> callback);

    void addToCart(Customer customer, Audiobook audiobook, AsyncCallback<Customer> callback);

    void updateAddressOfCustomer(Customer customer, String address, AsyncCallback<Customer> callback);

    void addPurchase(Customer customer, AsyncCallback<Customer> callback);

    void addContributor(String name, String bio, String DOB, AsyncCallback<Void> callback);



}
