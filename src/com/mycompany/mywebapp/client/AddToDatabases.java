package com.mycompany.mywebapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mycompany.mywebapp.shared.Audiobook;
import com.mycompany.mywebapp.shared.Customer;


@RemoteServiceRelativePath("addToDatabases")
public interface AddToDatabases extends RemoteService {

    Customer addCustomer(String name, String email, String password, String address);

    Customer addToCart(Customer customer, Audiobook audiobook);

    Customer updateAddressOfCustomer(Customer customer, String address);

    Customer addPurchase(Customer customer);

    void addContributor(String name, String bio, String DOB);

}