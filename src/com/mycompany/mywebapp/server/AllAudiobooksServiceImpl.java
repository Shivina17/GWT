package com.mycompany.mywebapp.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mycompany.mywebapp.client.AllAudiobooksService;
import com.mycompany.mywebapp.shared.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AllAudiobooksServiceImpl extends RemoteServiceServlet implements AllAudiobooksService {


    @Override
    public Audiobook[] getAllBooks(String in) {
        try {
            if (in.equalsIgnoreCase("TITLE") || in.equals("POPULAR") || in.equals("PRICE(L-H)") || in.equals("RATING")) {
                Audiobook audiobooks[] = MainConnect.mapToAudiobookObject(in);
                System.out.println("GET ALL BOOKS " + in+audiobooks.length);
                return audiobooks;
            } else if (in.startsWith("GENRE")) {
                String genre = in.substring(6);
                Audiobook audiobooks[] = MainConnect.getBookByGenre(genre);
                return audiobooks;
            } else {
                Audiobook audiobooks[] = MainConnect.getBookByClosestMatch(in);
                return audiobooks;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashMap<Contributor, Audiobook[]> getAllByAuthor(String s) {
        try {
            if (s.equalsIgnoreCase("ALL")) {
                HashMap<Contributor, Audiobook[]> audiobooks = MainConnect.getBooksForEachContributor("A", "ALL");
                return audiobooks;
            } else if (s.equalsIgnoreCase("POPULAR")) {
                HashMap<Contributor, Audiobook[]> audiobooks = MainConnect.getBooksForEachContributor("A", "POPULAR");
                return audiobooks;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    @Override
    public String[] getNamesOfAllBooksAuthorsForTitle() {
        try {
            String[] all = MainConnect.getNamesOfAllBooksAndContributors();
            return all;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String[] getAllGenres() {
        try {
            String[] genreItems = MainConnect.getAllGenres();
            return genreItems;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer validateCustomer(String email, String password) {
        try {
            Customer customer = MainConnect.validateCustomer(email, password);
                return customer;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Employee validateEmployee(String email, String password) {
        try {
            Employee employee = MainConnect.validateEmployee(email, password);
            return employee;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    }

    @Override
    public Purchase getPurchases(Customer customer) {
        try {
            Purchase purchases = MainConnect.getPurchaseOfCustomer(customer);
            return purchases;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    @Override
    public ArrayList<Audiobook> getRecommendations(ArrayList<Audiobook> purchasedBooks) {
        try{
            ArrayList<Audiobook> list = MainConnect.getRecommendations(purchasedBooks);
            return list;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashMap<String, Integer> getTrendMostBoughtAudiobooks() {
        try{
        HashMap<String, Integer> hashMap = MainConnect.getTrendsAudiobooks();
            return hashMap;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashMap<String, Integer> getTrendsBestCustomers() {
        try{
            HashMap<String, Integer> hashMap = MainConnect.getTrendsCustomers();
            return hashMap;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
//todo trends - get number of sales per year
//todo        - get customer who bought the most
