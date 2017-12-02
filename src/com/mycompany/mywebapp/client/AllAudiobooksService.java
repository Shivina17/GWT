package com.mycompany.mywebapp.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mycompany.mywebapp.shared.*;

import java.util.ArrayList;
import java.util.HashMap;

@RemoteServiceRelativePath("allAudiobooks")
public interface AllAudiobooksService extends RemoteService {
    Audiobook[] getAllBooks(String in);

    HashMap<Contributor, Audiobook[]> getAllByAuthor(String s);

    String[] getNamesOfAllBooksAuthorsForTitle();

    String[] getAllGenres();

    Customer validateCustomer(String email, String password);

    Employee validateEmployee(String email, String password);

    Purchase getPurchases(Customer customer);

    ArrayList<Audiobook> getRecommendations(ArrayList<Audiobook> purchasedBooks);

    HashMap<String, Integer> getTrendMostBoughtAudiobooks();

    HashMap<String, Integer> getTrendsBestCustomers();


}
