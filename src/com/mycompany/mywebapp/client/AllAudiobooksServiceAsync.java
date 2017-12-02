package com.mycompany.mywebapp.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.mywebapp.shared.*;

import java.util.ArrayList;
import java.util.HashMap;


public interface AllAudiobooksServiceAsync {
    void getAllBooks(String in, AsyncCallback<Audiobook[]> callback);

    void getAllByAuthor(String s, AsyncCallback<HashMap<Contributor, Audiobook[]>> callback);

    void getNamesOfAllBooksAuthorsForTitle(AsyncCallback<String[]> callback);

    void getAllGenres(AsyncCallback<String[]> callback);

    void validateCustomer(String email, String password, AsyncCallback<Customer> callback);

    void validateEmployee(String email, String password, AsyncCallback<Employee> callback);

    void getPurchases(Customer customer, AsyncCallback<Purchase> callback);

    void getRecommendations(ArrayList<Audiobook> purchasedBooks, AsyncCallback<ArrayList<Audiobook>> callback);

    void getTrendMostBoughtAudiobooks(AsyncCallback<HashMap<String, Integer>> callback);

    void getTrendsBestCustomers(AsyncCallback<HashMap<String,Integer>> callback);


}
