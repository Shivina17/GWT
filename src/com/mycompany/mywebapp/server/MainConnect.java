package com.mycompany.mywebapp.server;

import com.mycompany.mywebapp.shared.*;

import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

public class MainConnect {

    public static Connection makeConnection() {
        Connection connection = null;
        try {
            FileInputStream propInputStream = new FileInputStream("properties.properties");
            Properties properties = new Properties();
            properties.load(propInputStream);
            propInputStream.close();

            String host = properties.getProperty("host");
            String port = properties.getProperty("port");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String db = properties.getProperty("db");

            StringBuilder builder = new StringBuilder();
            builder.append("jdbc:mysql://");
            builder.append(host);
            builder.append(":");
            builder.append(port);
            builder.append("/");
            builder.append(db);
            String URL = builder.toString();


            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(URL, username, password);  //creating a connection

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;

    }

    public static Audiobook[] mapToAudiobookObject(String orderBy) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();

        if (orderBy.equalsIgnoreCase("TITLE")) {
            orderBy = "audio_title";
        } else if (orderBy.equalsIgnoreCase("PRICE(L-H)")) {
            orderBy = "audio_price";
        } else if (orderBy.equalsIgnoreCase("POPULAR")) {
            String query = "Select audiobook.*, count(purchase.audio_ISBN) as count" +
                    " from audiobook inner join purchase on audiobook.audio_ISBN = purchase.audio_ISBN" +
                    " group by purchase.audio_ISBN order by count DESC";
            return getFiltered(query);
        } else if (orderBy.equalsIgnoreCase("RATING")) {
            String query = "select audiobook.* from audiobook inner join rating on rating.audio_ISBN = audiobook.audio_ISBN" +
                    " group by rating.audio_ISBN order by avg(rating.rating) desc";
            return getFiltered(query);
        } else {
            orderBy = "audio_title";
        }

        orderBy = " ORDER BY " + orderBy;

        try {
            statement = connection.createStatement();
            ResultSet countRs = statement.executeQuery("SELECT COUNT(*) FROM audiobook");
            countRs.next();
            int rowCount = countRs.getInt(1);

            ResultSet resultSet = statement.executeQuery("SELECT * from audiobook" + orderBy);

            ResultSetMetaData rsm = resultSet.getMetaData();
            int columns = rsm.getColumnCount(); //number of columns

            Audiobook audiobooks[] = new Audiobook[rowCount];
            int number = 0;

            while (resultSet.next()) {
                Audiobook audiobook = getResultantObjectForOneAudiobook(resultSet);
                audiobooks[number++] = audiobook;
            }

            return audiobooks;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return new Audiobook[]{};
    }

    private static Audiobook[] getFiltered(String query) throws SQLException {

        Statement statement = null;
        Connection connection = makeConnection();


        try {
            statement = connection.createStatement();
            ResultSet forCount = statement.executeQuery(query);


            System.out.println();
            ArrayList<Audiobook> list = new ArrayList<>();
            while (forCount.next()) {
                Audiobook audiobook = getResultantObjectForOneAudiobook(forCount);
                list.add(audiobook);
            }

            Audiobook audiobooks[] = new Audiobook[list.size()];
            for (int i = 0; i < audiobooks.length; i++) {
                audiobooks[i] = list.get(i);
            }

            return audiobooks;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return new Audiobook[]{};

    }

    public static HashMap<Contributor, Audiobook[]> getBooksForEachContributor(String type, String filter) throws SQLException {
        PreparedStatement statement = null;
        HashMap<Contributor, Audiobook[]> map = new LinkedHashMap<>();
        Connection connection = makeConnection();


        if (filter.equals("ALL") || filter.equals("")) {
            filter = " ORDER BY cont_name";
            statement = connection.prepareStatement("select cont_ID, GROUP_CONCAT(audio_ISBN SEPARATOR ',') from contribute where cont_type = ? group by cont_ID;");
        } else if (filter.equalsIgnoreCase("POPULAR")) {
            statement = connection.prepareStatement("select cont_ID, GROUP_CONCAT(audio_ISBN SEPARATOR ',') " +
                    "from contribute where audio_ISBN in " +
                    "(select t2.audio_ISBN from " +
                    "(select A.audio_ISBN, count(A.audio_ISBN) as num, CAST((count(A.audio_ISBN)*A.audio_price) " +
                    "AS decimal(38,2)) as total from purchase P inner join audiobook A on A.audio_ISBN = P.audio_ISBN " +
                    "group by A.audio_ISBN order by total DESC) t2) and cont_type = ? group by cont_ID");

            filter = "";
        }


        try {
            System.out.println();

//            statement = connection.createStatement();
            PreparedStatement statement1 = connection.prepareStatement("SELECT COUNT(*) from contribute where cont_type =?");
            statement1.setString(1, type);

            ResultSet countRs = statement1.executeQuery();
            countRs.next();

            statement.setString(1, type);

            ResultSet resultSet = statement.executeQuery();
            //Author authors[] = new Author[rowCount];
            int number = 0;
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
                String cont_ID = resultSet.getString(1);
                Statement statementAuthor = connection.createStatement();
                String s = "select * from contributor where cont_ID = " + cont_ID + filter;
                ResultSet rsAuthor = statementAuthor.executeQuery(s);

                Author author = null;
                while (rsAuthor.next()) {
                    author = new Author(rsAuthor.getString(4), rsAuthor.getString(2), rsAuthor.getString(3));
                }


                String bookIDs[] = resultSet.getString(2).split(",");
                Audiobook audiobooks[] = new Audiobook[bookIDs.length];
                for (int i = 0; i < bookIDs.length; i++) {
                    bookIDs[i] = bookIDs[i].trim();
                    Statement newSt = connection.createStatement();
                    ResultSet bookSet = newSt.executeQuery("select * from audiobook where audio_ISBN=" + bookIDs[i]);
                    while (bookSet.next()) {
                        Audiobook book = getResultantObjectForOneAudiobook(bookSet);
                        audiobooks[i] = book;
                    }
                }

                map.put(author, audiobooks);
            }
            return map;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }
        return null;
    }


    private static Audiobook getResultantObjectForOneAudiobook(ResultSet resultSet) throws SQLException {
        Audiobook audiobook = null;
        Connection connection = makeConnection();


        try {
            String audio_ISBN = resultSet.getString("audio_ISBN");

            Statement statementRating = connection.createStatement();
            String s = "SELECT * FROM rating where audio_ISBN = " + audio_ISBN;
            ResultSet resultSetRating = statementRating.executeQuery(s);
            ArrayList<Review> reviews = new ArrayList<>();
            while (resultSetRating.next()) {
                Review review = new Review(resultSetRating.getString(1), resultSetRating.getString(2), resultSetRating.getString(4));
                reviews.add(review);
            }


            Statement statementAuthor = connection.createStatement();
            s = "select * from contributor where cont_ID in " +
                    "(select cont_ID from contribute where audio_ISBN =" +
                    audio_ISBN + " and cont_type=\"A\")";
            ResultSet rsAuthor = statementAuthor.executeQuery(s);
            ArrayList<Author> authors = new ArrayList<>();

            //if(rsAuthor.next()) {
                while (rsAuthor.next()) {
                    Author author = new Author(rsAuthor.getString(4), rsAuthor.getString(2), rsAuthor.getString(3));
                    authors.add(author);
                }
//            }
//            else{
//                Author author = new Author();
//            }

            Statement statementNarrator = connection.createStatement();
            s = "select * from contributor where cont_ID in (select cont_ID from contribute where audio_ISBN =" +
                    audio_ISBN + " and cont_type=\"N\")";
            ResultSet rsNarrator = statementNarrator.executeQuery(s);
            Narrator narrator = null;
            while (rsNarrator.next()) {
                narrator = new Narrator(rsNarrator.getString(4), rsNarrator.getString(2), rsNarrator.getString(3));
            }

            String genres[] = resultSet.getString("audio_genre").split(",");
            for (int i = 0; i < genres.length; i++) {
                genres[i] = genres[i].trim();
            }

            String audio_image = resultSet.getString("audio_image");

            audiobook = new Audiobook(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(8), audio_image,
                    genres, authors, narrator, reviews);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.close();

        }


        return audiobook;
    }

    public static Customer validateCustomer(String email, String password) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();
        Customer customer;
        try {
            statement = connection.prepareStatement("SELECT * from customer where customer_email= ? and customer_password= ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                customer = mapToCustomerObject(resultSet);
                return customer;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;


    }

    public static Employee validateEmployee(String email, String password) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();
        Employee employee;
        try {
            statement = connection.prepareStatement("SELECT * from employee where email= ? and password= ?");
            statement.setString(1,email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                employee = mapToEmployeeObject(resultSet);
                return employee;
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (statement != null) statement.close();
            if(connection!=null) connection.close();

        }

        return null;


    }

    public static void addContributor(String name, String bio, String DOB) throws SQLException {
        Connection connection = makeConnection();
        PreparedStatement statement = null;
        Statement st = null;

        //    public Customer(String ID, String DOB, String name, String email, String[] phones,
        // String password, String address, ArrayList<Audiobook> audio_ISBNs) {


        try {
            String id = "";
            int num=0;
            st = connection.createStatement();
            ResultSet countRs = st.executeQuery("select max(cont_ID) from contributor");
            if (countRs.next()) {
                num = countRs.getInt(1);
            }


            num =  num + 1;
            id = num+"";

            statement = connection.prepareStatement("insert into contributor values (?, ?, ?, ?)");
            statement.setString(1, id);
            statement.setString(2, bio);
            statement.setString(3, DOB);
            statement.setString(4, name);
            statement.execute();

            //ResultSet set = statement.executeQuery("select * from contributor where cont_ID=" + id);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

    }

    public static Employee mapToEmployeeObject(ResultSet resultSet) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();
        Employee employee;


        try {

            employee = new Employee(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4));

            return employee;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (statement != null) statement.close();
            if(connection!=null) connection.close();

        }

        return null;

    }



    public static Customer addToCart(Customer customer, Audiobook audiobook) throws SQLException {


        PreparedStatement statement = null;
        Connection connection = makeConnection();

        try {
            statement = connection.prepareStatement("insert into cart values (?, ?)");
            statement.setString(1, customer.getID());
            statement.setString(2, audiobook.getIsbn());
            statement.execute();

            Statement updateStatement = connection.createStatement();
            ResultSet set = updateStatement.executeQuery("select * from customer where customer_ID=" + customer.getID());

            while (set.next())
                return mapToCustomerObject(set);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }


    public static Customer mapToCustomerObject(ResultSet resultSet) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();
        Customer customer;


        try {
            String phones[] = resultSet.getString("customer_phones").split(",");

            Statement statement1 = connection.createStatement();
            String s = "SELECT * FROM cart where customer_ID = " + resultSet.getString(1);
            ResultSet resultSet2 = statement1.executeQuery(s);
            ArrayList<Audiobook> books = new ArrayList<>();

            while (resultSet2.next()) {

                String isbn = resultSet2.getString(2);
                System.out.println("CART ? " + isbn);

                Statement statementBook = connection.createStatement();
                s = "SELECT * FROM audiobook where audio_ISBN = " + resultSet2.getString(2);
                ResultSet resultSet3 = statementBook.executeQuery(s);

                while (resultSet3.next()) {
                    Audiobook book = getResultantObjectForOneAudiobook(resultSet3);
                    books.add(book);
                }
            }
            customer = new Customer(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                    resultSet.getString(4), phones, resultSet.getString(6), resultSet.getString(7), books);

            return customer;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }


    public static Customer[] getDetailsOfAllCustomers() throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();

        try {

            statement = connection.createStatement();
            ResultSet countRs = statement.executeQuery("SELECT COUNT(*) FROM customer");
            countRs.next();
            int rowCount = countRs.getInt(1);

            ResultSet resultSet = statement.executeQuery("SELECT * from customer");

            Customer customers[] = new Customer[rowCount];
            int number = 0;
            while (resultSet.next()) {
                Customer customer = mapToCustomerObject(resultSet);
                customers[number++] = customer;
                System.out.println();
            }

            return customers;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;
    }


    public static String[] getNamesOfAllBooksAndContributors() throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();

        try {

            statement = connection.createStatement();
            ResultSet countRs = statement.executeQuery("select sum((select count(*) from audiobook) +" +
                    " (select count(*) from contributor))");
            countRs.next();
            int rowCount = countRs.getInt(1);


            ResultSet resultAllContributors = statement.executeQuery("SELECT cont_name from contributor");


            String names[] = new String[rowCount];
            int number = 0;
            while (resultAllContributors.next()) {
                String name = resultAllContributors.getString(1);
                names[number++] = name;
            }

            Statement statement1 = connection.createStatement();
            ResultSet resultSetAllBooks = statement1.executeQuery("SELECT audio_title from audiobook");
            while (resultSetAllBooks.next()) {
                String name = resultSetAllBooks.getString(1);
                names[number++] = name;
            }


            return names;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return new String[]{};
    }


    public static String[] getAllGenres() throws SQLException {
        String genres[];
        Connection connection = makeConnection();
        Statement statement = null;


        try {

            statement = connection.createStatement();
            ResultSet resultSetGenres = statement.executeQuery("select audio_genre from audiobook");

            HashSet<String> genreSet = new HashSet();
            while (resultSetGenres.next()) {
                String name = resultSetGenres.getString(1);

                String gens[] = name.split(",");
                for (String genre : gens) {
                    genre = genre.trim();
                    genreSet.add(genre);
                }
            }

            genres = new String[genreSet.size()];
            int num = 0;
            for (String genre : genreSet) {
                genres[num++] = genre;
            }

            System.out.println(genreSet.toString());

            return genres;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
        }
        return new String[]{};

    }

    public static Audiobook[] getBookByGenre(String genre) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();


        try {
            statement = connection.prepareStatement("SELECT * FROM audiobook where LOWER(audio_genre) like LOWER(?)");
            statement.setString(1, "%" + genre + "%");
            ResultSet resultSetRating = statement.executeQuery();
            ArrayList<Audiobook> books = new ArrayList<>();

            while (resultSetRating.next()) {
                System.out.println("GENRE ");
                Audiobook book = getResultantObjectForOneAudiobook(resultSetRating);
                books.add(book);
            }
            Audiobook audiobooks[] = new Audiobook[books.size()];
            for (int i = 0; i < audiobooks.length; i++) {
                audiobooks[i] = books.get(i);
            }

            return audiobooks;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }
        return null;
    }

    public static Audiobook[] getBookByClosestMatch(String wordEntered) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();


        ArrayList<Audiobook> books = new ArrayList<>();
        HashSet<Audiobook> audioSet = new HashSet<>();

        try {
            statement = connection.prepareStatement("SELECT * FROM audiobook where " +
                    "LOWER(audio_title) like LOWER(?)");
            statement.setString(1, "%" + wordEntered + "%");
            ResultSet resultSetRating = statement.executeQuery();

            while (resultSetRating.next()) {
                Audiobook book = getResultantObjectForOneAudiobook(resultSetRating);
                System.out.println(book.getAudioTitle());
                books.add(book);
            }

            HashMap<Contributor, Audiobook[]> mapAuth = getBooksForEachContributor("A", "");

            for (Contributor con : mapAuth.keySet()) {
                String conName = con.getCont_name();
                if (conName.toLowerCase().contains(wordEntered.toLowerCase())) {
                    Audiobook matchedBooks[] = mapAuth.get(con);
                    for (Audiobook bk : matchedBooks) {
                        audioSet.add(bk);
                    }
                }

            }

            HashMap<Contributor, Audiobook[]> mapNar = getBooksForEachContributor("N", "");
            for (Contributor con : mapNar.keySet()) {
                String conName = con.getCont_name();

                if (conName.toLowerCase().contains(wordEntered.toLowerCase())) {
                    Audiobook matchedBooks[] = mapNar.get(con);
                    for (Audiobook bk : matchedBooks) {
                        audioSet.add(bk);
                    }
                }

            }

            books.addAll(audioSet);

            Audiobook audiobooks[] = new Audiobook[books.size()];
            for (int i = 0; i < audiobooks.length; i++) {
                audiobooks[i] = books.get(i);
            }

            return audiobooks;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }
        return null;

    }


    public static Customer removeFromCart(Customer customer, Audiobook audiobook) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();

        try {
            statement = connection.prepareStatement("delete from cart where customer_ID = ? and audio_ISBN = ? ");
            statement.setString(1, customer.getID());
            statement.setString(2, audiobook.getIsbn());
            statement.execute();

            Statement updateStatement = connection.createStatement();
            ResultSet set = updateStatement.executeQuery("select * from customer where customer_ID=" + customer.getID());

            while (set.next())
                return mapToCustomerObject(set);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }


    public static Purchase[] getPurchaseAllCustomers() throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();
        Purchase purchase = null;

        ArrayList<Purchase> list = new ArrayList<>();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT customer_ID, GROUP_CONCAT(audio_ISBN SEPARATOR ','), purchase_date from purchase group by customer_ID");

            while (resultSet.next()) {
                purchase = getPurchaseOfCustomer(resultSet);

                list.add(purchase);
            }


            Purchase purchases[] = new Purchase[list.size()];
            for (int i = 0; i < list.size(); i++) {
                purchases[i] = list.get(i);
            }


            return purchases;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }

    public static Purchase getPurchaseOfCustomer(Customer customer) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();
        Purchase purchase = null;

        try {

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select customer_ID, GROUP_CONCAT(audio_ISBN separator ','), purchase_date from purchase where customer_ID = " + customer.getID());
            while (resultSet.next()) {
                purchase = getPurchaseOfCustomer(resultSet);
            }

            return purchase;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;
    }

    public static Purchase getPurchaseOfCustomer(ResultSet resultSet) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();
        Purchase purchase = null;
        Customer customer = null;
        ArrayList<Audiobook> books = new ArrayList<>();

        try {
            Statement statement1 = connection.createStatement();
            String s = "SELECT * FROM customer where customer_ID = " + resultSet.getString(1);
            ResultSet resultSet2 = statement1.executeQuery(s);
            while (resultSet2.next()) {
                customer = mapToCustomerObject(resultSet2);
            }
            System.out.println(customer.getName());

            String[] audioIDs = resultSet.getString(2).split(",");

            for (String bookID : audioIDs) {
                Statement statementBook = connection.createStatement();
                s = "SELECT * FROM audiobook where audio_ISBN = " + bookID;
                ResultSet resultSet3 = statementBook.executeQuery(s);

                while (resultSet3.next()) {
                    Audiobook book = getResultantObjectForOneAudiobook(resultSet3);
                    books.add(book);
                }
            }

            Audiobook audiobooks[] = new Audiobook[books.size()];
            for (int i = 0; i < books.size(); i++) {
                audiobooks[i] = books.get(i);
            }

            purchase = new Purchase(customer, audiobooks, resultSet.getString(3));
            return purchase;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }

        return null;
    }


    public static Customer updateCustomerAddress(Customer customer, String newAddress) throws SQLException {


        PreparedStatement statement = null;
        Connection connection = makeConnection();

        try {
            statement = connection.prepareStatement("update customer set customer_address = ? where customer_ID=" + customer.getID());
            statement.setString(1, newAddress);
            statement.execute();

            Statement updateStatement = connection.createStatement();
            ResultSet set = updateStatement.executeQuery("select * from customer where customer_ID=" + customer.getID());

            while (set.next())
                return mapToCustomerObject(set);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;
    }


    public static ArrayList<Audiobook> getCart(Customer customer) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = makeConnection();

        try {
            Statement statement1 = connection.createStatement();
            String s = "SELECT * FROM cart where customer_ID = " + customer.getID();
            ResultSet resultSet2 = statement1.executeQuery(s);
            ArrayList<Audiobook> books = new ArrayList<>();

            while (resultSet2.next()) {

                Statement statementBook = connection.createStatement();
                s = "SELECT * FROM audiobook where audio_ISBN = " + resultSet2.getString(2);
                ResultSet resultSet3 = statementBook.executeQuery(s);

                while (resultSet3.next()) {
                    Audiobook book = getResultantObjectForOneAudiobook(resultSet3);
                    books.add(book);
                }
            }

            return books;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;
    }

    public static Customer purchaseBooks(Customer customer) throws SQLException {

        ArrayList<Audiobook> books = getCart(customer);
        Customer updatedCustomer = null;

        for (Audiobook book : books) {
            addPurchase(customer, book);
            updatedCustomer = removeFromCart(customer, book);
        }
        return updatedCustomer;


    }

    public static Purchase addPurchase(Customer customer, Audiobook audiobook) throws SQLException {

        Connection connection = makeConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("insert into purchase values (?, ?, ?)");
            statement.setString(1, customer.getID());
            statement.setString(2, audiobook.getIsbn());
            Timestamp date = new Timestamp(System.currentTimeMillis());
            String d = date.toString().substring(0, 10);
            statement.setString(3, "2017-03-03");
            statement.execute();

            Statement updateStatement = connection.createStatement();
            ResultSet set = updateStatement.executeQuery("select * from purchase where customer_ID=" + customer.getID());

            while (set.next())
                return getPurchaseOfCustomer(set);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }

    public static Customer addCustomer(String name, String email, String password, String address) throws SQLException {

        Connection connection = makeConnection();
        PreparedStatement statement = null;
        Statement st = null;

        try {
            String id = "";
            int num=0;
            st = connection.createStatement();
            ResultSet countRs = st.executeQuery("select max(customer_ID) from customer");
            if (countRs.next()) {
                num = countRs.getInt(1);
            }


            num =  num + 1;
            id = num+"";
            String DOB = null;
            String phones[] = new String[]{"unlisted"};
            ArrayList<Audiobook> audiobooks = new ArrayList<>();

            //Customer customer = new Customer(id, DOB, name, email, phones, password, address, audiobooks);

            statement = connection.prepareStatement("insert into customer values (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, id);
            statement.setString(2, DOB);
            statement.setString(3, name);
            statement.setString(4, email);
            statement.setString(5, "unlisted");
            statement.setString(6, password);
            statement.setString(7, address);
            statement.execute();

            ResultSet set = statement.executeQuery("select * from customer where customer_ID=" + id);
            while (set.next())
                return mapToCustomerObject(set);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return null;

    }

    public static ArrayList<Audiobook> getRecommendations(ArrayList<Audiobook> list){
        ArrayList<Audiobook> audiobookSet = new ArrayList<>();
        HashSet<String> genreSet = new HashSet<>();



        try {
            for (Audiobook book : list) {
                String genres[] = book.getGenres();
                for (String genre : genres) {
                    genreSet.add(genre);
                }
            }



            for (String genre : genreSet) {
                Audiobook[] list1 = getBookByGenre(genre);
                for(Audiobook bk : list1){
                    audiobookSet.add(bk);
                }
            }

//            for(Audiobook bk : audioSet){
//                System.out.println(bk.getAudioTitle());
//            }

            audiobookSet = getUnique(audiobookSet, list);

            return audiobookSet;


        }
        catch(Exception e){

        }

        return null;

    }

    public static ArrayList<Audiobook> getUnique(ArrayList<Audiobook> books, ArrayList<Audiobook> purchasedList){

        ArrayList<Audiobook> newList = new ArrayList<>();
        for(Audiobook audiobook: books){
            boolean inList = false;
            for(Audiobook pb: purchasedList) {
                if (audiobook.getIsbn() == pb.getIsbn()){
                    //in list
                    inList=true;
                }
            }

            if(!inList){
                newList.add(audiobook);
            }
        }
        return newList;
    }

    public static HashMap<String, Integer> getTrendsAudiobooks() throws SQLException {
        String query1 = "select audio_ISBN, GROUP_CONCAT(customer_ID separator \",\") " +
                "as \"customers\", count(\"customers\") as count " +
                "from purchase group by audio_ISBN order by count desc;";
        String query2 = "select audio_title from audiobook where audio_ISBN=?";


        HashMap<String, Integer> hashMap = getTrends(query1, query2);
        return hashMap;

    }

    public static HashMap<String, Integer> getTrendsCustomers() throws SQLException {

        System.out.println();
        String query1 =  "select customer_ID, GROUP_CONCAT(audio_ISBN separator \",\") as \"books\", " +
                "count(\"books\") from purchase group by customer_ID order by count(\"books\") desc;";
        String query2 = "select customer_name from customer where customer_ID=?";
        HashMap<String, Integer> hashMap = getTrends(query1, query2);
        return hashMap;
    }


    public static HashMap<String, Integer> getTrends(String query1, String query2) throws SQLException {
        Statement statement = null;
        Connection connection = makeConnection();

        HashMap<String, Integer> hashMap = new LinkedHashMap<>();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query1);

            while(resultSet.next()){
                String ID = resultSet.getString(1);
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                preparedStatement.setString(1, ID);

                ResultSet resultSet1 = preparedStatement.executeQuery();

                String audio_title="";
                if(resultSet1.next()){
                    audio_title = resultSet1.getString(1);
                }

                String number = resultSet.getString(3);
                int n = Integer.parseInt(number);
                hashMap.put(audio_title, n);
            }



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null) connection.close();

        }

        return hashMap;
    }






    public static void main(String args[]) {
        try {
            String[] allNames = getNamesOfAllBooksAndContributors();
            Customer customers[] = getDetailsOfAllCustomers();
            //System.out.println(Arrays.toString(allNames));

            //     Customer customer = updateCustomerAddress(null, "61 West Avenue, CA");
//            System.out.println(customer.getName()+ " "+customer.getAddress());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
