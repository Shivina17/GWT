package com.mycompany.mywebapp.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.mycompany.mywebapp.shared.*;

import java.util.ArrayList;
import java.util.HashMap;



public class Listing {


    //static int c1 =0;
    static MenuItem cartStatic;
    static int unknownCust;

    static Customer customerLoggedIn;
    static Employee employeeLoggedIn;

    static ArrayList<Audiobook> cartBooks = new ArrayList<>();

    private static final AllAudiobooksServiceAsync allBooksService = GWT.create(AllAudiobooksService.class);
    private static final AddToDatabasesAsync addService = GWT.create(AddToDatabases.class);

    private static Grid popularGrid;

    public Listing() {
    }

    public void init() {
        RootPanel.get("infoPanel").getElement().setInnerHTML("");
    }

    public static void getViewPanelByPopularity() {


//        //get by popularity
        final Audiobook popular[] = new Audiobook[4];
        allBooksService.getAllBooks("POPULAR", new AsyncCallback<Audiobook[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Audiobook[] audiobooks) {
                if (audiobooks.length > 0) {
                    for (int i = 0; i < audiobooks.length; i++) {
                        popular[i] = (audiobooks[i]);

                    }
                    Element paraHead = DOM.getElementById("infoPanelMessageH3");
                    paraHead.setInnerHTML("POPULAR TODAY");
                    Element para = DOM.getElementById("infoPanelMessageP");
                    para.setInnerHTML("Welcome to BookStream! We offer a vast range of audiobooks for purchase and streaming. Browse/sign in to explore.");

                    Grid grid = getViewPanel(popular);
                    VerticalPanel panel= new VerticalPanel();
                    panel.add(new HTML("<b style=\"font-size: 25px;\">POPULAR TODAY</b>"));
                    panel.add(grid);
                    RootPanel.get("infoPanel").getElement().setInnerHTML("");
                    RootPanel.get("infoPanel").add(panel);
                } else {
                    Window.alert("No books in DB!");
                }
            }
        });



    }

    public static Grid getViewPanel(Audiobook[] popularBooks) {
        Grid grid = new Grid(4, 2);

        int c = 0;
        int numRows = grid.getRowCount();
        int numColumns = grid.getColumnCount();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {

                if (row % 2 == 0) {
                    final Image image = new Image(popularBooks[row+col].getAudio_image(), 10, 10, 400, 300);
                    grid.setWidget(row, col, image);
                    grid.setCellSpacing(20);

                    final Audiobook audiobook = popularBooks[row + col];
                    image.addMouseOverHandler(new MouseOverHandler() {
                        @Override
                        public void onMouseOver(MouseOverEvent mouseOverEvent) {
                            image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                        }
                    });

                    image.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            //display page
                            Panel newGrid = displayInfoForClickedBook(audiobook);
                            RootPanel.get("infoPanel").getElement().setInnerHTML("");
                            RootPanel.get("infoPanel").add(newGrid);
                        }
                    });
                } else {

                    VerticalPanel descPanel = new VerticalPanel();

                    final Label title = new Label(popularBooks[c].getAudioTitle());
                    title.addStyleName("gwt-MyLabel");
                    final Audiobook audiobook = popularBooks[c];

                    title.addMouseOverHandler(new MouseOverHandler() {
                        @Override
                        public void onMouseOver(MouseOverEvent mouseOverEvent) {
                            title.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                        }
                    });
                    title.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            Panel newGrid = displayInfoForClickedBook(audiobook);
                            RootPanel.get("infoPanel").getElement().setInnerHTML("");
                            RootPanel.get("infoPanel").add(newGrid);
                        }
                    });
                    descPanel.add(title);


                    Label narratedByLabel = new Label("\nNarrated by ");
                    descPanel.add(narratedByLabel);
                    final Narrator narratorObj = popularBooks[c].getNarrator();

                    if (narratorObj != null) {
                        final String nName = popularBooks[c].getNarrator().getCont_name();
                        final Label narrator = new Label(nName);
                        descPanel.add(narrator);
                    } else {
                        descPanel.add(new HTML("<b style = \"font-style: italic;\">Not Listed</b>"));
                    }


                    final Label publisher = new Label("Published by " + popularBooks[c].getPub_ID());
                    descPanel.add(publisher);

                    Label genreLabel = new Label("\nGenres ");
                    descPanel.add(genreLabel);

                    String genres[] = popularBooks[c].getGenres();
                    HorizontalPanel genrePanel = new HorizontalPanel();
                    for (String genre : genres) {
                        final Label oneGenre = new Label(genre);
                        //authorNamesLabel.removeStyleName("gwt-Label");
                        oneGenre.addStyleName("gwt-MyLabelAuthor");
                        genrePanel.add(oneGenre);
                    }
                    descPanel.add(genrePanel);

                    Label writtenByLabel = new Label("\nWritten by ");
                    descPanel.add(writtenByLabel);

                    ArrayList<Author> authors = popularBooks[c].getAuthors();
                    HorizontalPanel authorNamesPanel = new HorizontalPanel();
                    if (authors.size() == 0 || authors == null) {

                        for (Author author : authors) {
                            final Label authorNamesLabel = new Label(author.getCont_name());
                            //authorNamesLabel.removeStyleName("gwt-Label");
                            authorNamesLabel.addStyleName("gwt-MyLabelAuthor");
                            authorNamesPanel.add(authorNamesLabel);
                        }
                    } else {
                        authorNamesPanel.add(new HTML("<b style = \"font-style: italic;\">Not Listed</b>"));
                    }

                    descPanel.add(authorNamesPanel);
                    Label labelISBN = new Label();
                    String s = "\nISBN - " + popularBooks[c].getIsbn();
                    labelISBN.setText(s);
                    descPanel.add(labelISBN);

                    Label labelPrice = new Label();
                    s = "PRICE - £" + popularBooks[c].getPrice();
                    labelPrice.setText(s);
                    descPanel.add(labelPrice);

                    Label labelDate = new Label();
                    s = "PUBLICATION DATE - " + popularBooks[c].getPubDate();
                    labelDate.setText(s);
                    descPanel.add(labelDate);


                    Label ageLabel = new Label();
                    if (audiobook.getAgeRate() != null) {
                        s = "AGE RATING - " + audiobook.getAgeRate();
                    } else {
                        s = "AGE RATING - Family friendly!";
                    }
                    ageLabel.setText(s);
                    descPanel.add(ageLabel);

                    grid.setWidget(row, col, descPanel);
                    grid.setCellPadding(30);
                    c++;
                }
            }

        }

        Element paraHead = DOM.getElementById("infoPanelMessageH3");
        paraHead.setInnerHTML("LISTEN. EXPLORE.");
        Element para = DOM.getElementById("infoPanelMessageP");
        para.setInnerHTML("Welcome to BookStream! We offer a vast range of audiobooks for purchase and streaming. Browse to explore.");


        // Return the panel
        grid.ensureDebugId("cwGrid");
        return grid;
    }


    public static Panel getViewsForSearches(HashMap<Contributor, Audiobook[]> map) {
        // Grid grid = new Grid(books.si, 2);

        VerticalPanel panel = new VerticalPanel();
        for (Contributor contributor : map.keySet()) {
            VerticalPanel inPanel = new VerticalPanel();

            final Label authorName = new Label(contributor.getCont_name());
            authorName.addStyleName("gwt-MyLabelList");
            inPanel.add(authorName);


            final Label authorDesc = new Label(contributor.getCont_desc());
            authorDesc.addStyleName("gwt-authDesc");
            inPanel.add(authorDesc);

            Audiobook books[] = map.get(contributor);

            HorizontalPanel horizontalPanel = new HorizontalPanel();

            VerticalPanel panel1 = new VerticalPanel();
            for (Audiobook book : books) {
                Panel grid = displayInfoForClickedBook(book);
                panel1.add(grid);
            }
            horizontalPanel.add(panel1);

            inPanel.add(horizontalPanel);
            panel.add(inPanel);
        }

        Element paraHead = DOM.getElementById("infoPanelMessageH3");
        paraHead.setInnerHTML("LISTEN. EXPLORE.");
        Element para = DOM.getElementById("infoPanelMessageP");
        para.setInnerHTML("Welcome to BookStream! We offer a vast range of audiobooks for purchase and streaming. Browse to explore.");



        return panel;
    }

    public static Panel displayInfoForClickedBook(final Audiobook audiobook) {
        Grid grid = new Grid(3, 2);


        VerticalPanel wholePanel = new VerticalPanel();

        final Image image = new Image(audiobook.getAudio_image(), 10, 10, 400, 300);
        grid.setWidget(0, 0, image);




        VerticalPanel descPanel = new VerticalPanel();

        descPanel.add(new HTML("<b style=\"font-size: 23px; padding-left: 30px; padding-top: -40px\">" + audiobook.getAudioTitle() + "</b>"));

        //  Label titleLabel = new Label(audiobook.getAudioTitle());
        // titleLabel.removeStyleName("gwt-Label");
        // titleLabel.addStyleName("gwt-MyLabelStandAlone");
        // titleLabel.addStyleName("gwt-style");
        // descPanel.add(titleLabel);

        final Label narrator = new Label("Narrated by ");
        descPanel.add(narrator);
        narrator.setStyleName("gwt-LabelNarratorStandAlone");
        final Narrator narratorObj = audiobook.getNarrator();

        if (narratorObj != null) {
            final String nName = audiobook.getNarrator().getCont_name();
            final Label narratedByLabel = new Label(nName);
            narratedByLabel.setStyleName("gwt-LabelNarratorStandAlone");

            descPanel.add(narratedByLabel);
        } else {
            descPanel.add(new HTML("<b style = \"font-style: italic; font-size: 13px; margin-left: 30px;\">Not Listed</b>"));
        }


        final Label publisher = new Label("Published by " + audiobook.getPub_ID());
        publisher.setStyleName("gwt-LabelNarratorStandAlone");
        descPanel.add(publisher);

        Label genreLabel = new Label("\nGenres ");
        genreLabel.setStyleName("gwt-LabelNarratorStandAlone");
        descPanel.add(genreLabel);

        String genres[] = audiobook.getGenres();
        HorizontalPanel genrePanel = new HorizontalPanel();
        for (String genre : genres) {
            final Label oneGenre = new Label(genre);
            oneGenre.addStyleName("gwt-MyLabelAuthorStandAlone");
            genrePanel.add(oneGenre);
        }
        descPanel.add(genrePanel);

        Label writtenByLabel = new Label("\nWritten by ");
        writtenByLabel.setStyleName("gwt-LabelNarratorStandAlone");
        descPanel.add(writtenByLabel);

        ArrayList<Author> authors = audiobook.getAuthors();
        HorizontalPanel authorNamesPanel = new HorizontalPanel();

        if (authors.size() != 0) {

            for (Author author : authors) {
                final Label authorNamesLabel = new Label(author.getCont_name());
                authorNamesLabel.addStyleName("gwt-MyLabelAuthorStandAlone");

                authorNamesPanel.add(authorNamesLabel);
            }
        } else {
            authorNamesPanel.add(new HTML("<b style = \"font-style: italic; font-size: 13px; margin-left: 30px;\">Not Listed</b>"));
        }


        descPanel.add(authorNamesPanel);


        Label labelISBN = new Label();
        String s = "\nISBN - " + audiobook.getIsbn();
        labelISBN.setStyleName("gwt-LabelNarratorStandAlone");
        labelISBN.setText(s);
        descPanel.add(labelISBN);

        Label labelPrice = new Label();
        s = "PRICE - £" + audiobook.getPrice();
        labelPrice.setStyleName("gwt-LabelNarratorStandAlone");
        labelPrice.setText(s);
        descPanel.add(labelPrice);

        Label labelDate = new Label();
        s = "PUBLICATION DATE - " + audiobook.getPubDate();
        labelDate.setText(s);
        descPanel.add(labelDate);

        Label ageLabel = new Label();
        if (audiobook.getAgeRate() != null) {
            s = "AGE RATING - " + audiobook.getAgeRate();
        } else {
            s = "AGE RATING - Family friendly!";
        }
        ageLabel.setText(s);
        descPanel.add(ageLabel);


        grid.setWidget(0, 1, descPanel);
        grid.setCellPadding(30);


        final Button addToCartButton = new Button();
        addToCartButton.setPixelSize(200, 50);
        addToCartButton.setText("Add to cart");
        addToCartButton.removeStyleName("gwt-Button");
        addToCartButton.addStyleName("gwt-PurchaseButton");
        grid.setWidget(1, 0, addToCartButton);

        addToCartButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                addToCartButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            }
        });


        addToCartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {


                if (MyWebApp.loggedInStatus) {

                    final Audiobook audioToAdd = audiobook;
                    customerLoggedIn = MyWebApp.customerLoggedIn;

                    addService.addToCart(customerLoggedIn, audiobook, new AsyncCallback<Customer>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert(throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(Customer customer) {
                            customerLoggedIn = customer;
                            MyWebApp.cart.setText("MY CART(" + customerLoggedIn.getAudio_ISBNs().size() + ")");

                            MyWebApp.checkoutButton.setEnabled(true);
                            final ArrayList<Audiobook> list = customerLoggedIn.getAudio_ISBNs();
                            MyWebApp.checkoutButton.addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent clickEvent) {
                                    addToPurchase(list);
                                }
                            });


                            class MyCartCommand implements Command {

                                Audiobook audiobook2;

                                public MyCartCommand(Audiobook audiobook2) {
                                    this.audiobook2 = audiobook2;
                                }

                                @Override
                                public void execute() {
                                    Panel panel = Listing.displayInfoForClickedBook(audiobook2);
                                    RootPanel.get("infoPanel").getElement().setInnerHTML("");
                                    RootPanel.get("infoPanel").add(panel);
                                }
                            }


                            MyWebApp.cartBar.addItem(audioToAdd.getAudioTitle(), new MyCartCommand(audioToAdd));

                        }
                    });
                } else {
                    MyWebApp.customerLoggedIn = null;
                    final DialogBox box = new DialogBox();
                    VerticalPanel checkoutPanel = new VerticalPanel();
                    checkoutPanel.add(new HTML("<b style=\"color:red;\">You need to log in/sign up before you can add to cart.</b>"));
                    final Button cancelButton = new Button("Close");
                    cancelButton.getElement().setId("closeButton");
                    checkoutPanel.add(cancelButton);

                    cancelButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            box.hide();
                        }
                    });

                    box.add(checkoutPanel);
                    box.center();
                }
            }
        });

        grid.ensureDebugId("cwGrid");
        wholePanel.add(grid);

        final Label rl = new Label("REVIEWS");
        rl.addStyleName("gwt-MyLabelStandAloneReviews");
        wholePanel.add(rl);

        ArrayList<Review> reviews = audiobook.getReviews();
        HorizontalPanel allReviews = new HorizontalPanel();

        if (reviews.size() != 0) {
            allReviews.setSpacing(20);
            for (Review review : reviews) {
                VerticalPanel reviewPanel = new VerticalPanel();

                final Label ratingLabel = new Label("Rating- " + review.getRating() + "");
                ratingLabel.addStyleName("gwt-authDesc");
                reviewPanel.add(ratingLabel);

                final Label descLabel = new Label("Comment- \"" + review.getComment() + "\"");
                descLabel.addStyleName("gwt-authDesc");
                reviewPanel.add(descLabel);

                final Label cust = new Label("Given By- " + review.getCustomer_ID());
                cust.addStyleName("gwt-authDesc");
                reviewPanel.add(cust);

                allReviews.add(reviewPanel);

            }
        } else {
            allReviews.add(new HTML("<b style = \"font-style: italic; font-size: 13px; margin-left: 30px;\">No reviews currently.</b>"));
        }

        wholePanel.add(allReviews);

        Element paraHead = DOM.getElementById("infoPanelMessageH3");
        paraHead.setInnerHTML("LISTEN. EXPLORE.");
        Element para = DOM.getElementById("infoPanelMessageP");
        para.setInnerHTML("Welcome to BookStream! We offer a vast range of audiobooks for purchase and streaming. Browse to explore.");


        return wholePanel;
    }


//    public static ArrayList<Audiobook> getRecommendations(ArrayList<Audiobook> list){
//
//    }


    public static void displayCustomerInfo(Customer customer2) {

        MyWebApp.inMyProfilepage = true;
        final  Panel wholePanel = new VerticalPanel();

        VerticalPanel infoPanel = new VerticalPanel();
        final VerticalPanel purchasePanel = new VerticalPanel();

        Label customerWelcome = new Label("Welcome, " + customer2.getName());
        customerWelcome.removeStyleName("gwt-Label");
        customerWelcome.addStyleName("gwt-MyLabel2");

        Label registeredEmail = new Label("Your registered Email Address : ");
        registeredEmail.setText(customer2.getEmail());
        registeredEmail.addStyleName("gwt-MyLabel3");

        Label detsLabel = new Label("Your registered Home Address : ");
        detsLabel.addStyleName("gwt-MyLabel3");

        TextArea addressTextArea = new TextArea();
        addressTextArea.setText(customer2.getAddress());
        addressTextArea.setVisibleLines(6);
        addressTextArea.setEnabled(false);

        addressTextArea.removeStyleName("gwt-TextArea");
        addressTextArea.addStyleName("gwt-TextAreaMyAreaStandAlone");

        final Button addAddressButton = new Button();
        addAddressButton.setPixelSize(150, 100);
        addAddressButton.setText("Update address");
        addAddressButton.removeStyleName("gwt-Button");
        addAddressButton.addStyleName("gwt-PurchaseButton");

        addAddressButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                addAddressButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            }
        });
        addAddressButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final DialogBox box = new DialogBox();
                box.setAnimationEnabled(true);
                VerticalPanel dialogVPanel = new VerticalPanel();

                dialogVPanel.add(new HTML("<b>Enter address:</b>"));
                dialogVPanel.setPixelSize(350, 400);
                final TextArea addressText = new TextArea();
                addressText.setPixelSize(300, 300);
                addressText.setText("London");
                addressText.ensureDebugId("cwBasicText-textbox");
                dialogVPanel.add(addressText);

                final Button okButton = new Button("Update");
                okButton.getElement().setId("closeButton");

                final Button cancelButton = new Button("Cancel");
                cancelButton.getElement().setId("closeButton");

                cancelButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        box.hide();
                    }
                });


                dialogVPanel.add(okButton);
                dialogVPanel.add(cancelButton);
                box.add(dialogVPanel);
                box.center();

                okButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        final String address = addressText.getText();
                        box.hide();
                        updateAddress(address);

                    }
                });
            }
        });


        infoPanel.add(customerWelcome);
        infoPanel.add(detsLabel);
        infoPanel.add(addressTextArea);
        infoPanel.add(addAddressButton);


        Label yourPurchase = new Label("Your Purchases ");
        yourPurchase.removeStyleName("gwt-Label");
        yourPurchase.addStyleName("gwt-MyLabel4");
        purchasePanel.add(yourPurchase);

        allBooksService.getPurchases(customer2, new AsyncCallback<Purchase>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Purchase purchase) {
                if (purchase != null) {
                    final Audiobook audiobooks[] = purchase.getAudiobooks();
                    Panel panel = new VerticalPanel();
                    // panel.add(new HTML("<p><b><h3>"+customerLoggedIn.getName()+", you can now stream the videos on the following links.</h3></b></p>"));
                    for (int i = 0; i < audiobooks.length; i++) {
                        panel.add(new HTML("<b style=\"font-size: 15px;\">Purchase date : " + purchase.getDate() + "</b>"));
                        Anchor anchor = new Anchor(audiobooks[i].getAudioTitle(), audiobooks[i].getFileLoc());
                        panel.add(anchor);

                        Panel newGrid = Listing.displayInfoForClickedBook(audiobooks[i]);
                        panel.add(newGrid);
                    }
                    purchasePanel.add(panel);


                    final ArrayList<Audiobook> purchasedBooks = getUnique(audiobooks);

                    allBooksService.getRecommendations(purchasedBooks, new AsyncCallback<ArrayList<Audiobook>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert(throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(ArrayList<Audiobook> list) {
                            list = getUnique(list, purchasedBooks);

                            final VerticalPanel suggestionsForYouPanel = new VerticalPanel();
                            suggestionsForYouPanel.add(new HTML("<b style=\"font-size: 25px; color: red;\">Recommended for you</b>"));
                            for(Audiobook a: list) {
                                Panel panel1 = displayInfoForClickedBook(a);
                                suggestionsForYouPanel.add(panel1);
                            }
                            wholePanel.add(suggestionsForYouPanel);
                        }
                    });

                } else {
                    Label none = new Label("No items bought. \uD83D\uDE22");
                    none.removeStyleName("gwt-Label");
                    none.addStyleName("gwt-MyLabel4");
                    purchasePanel.add(none);
                }
            }
        });





        wholePanel.add(infoPanel);
        wholePanel.add(purchasePanel);
        RootPanel.get("infoPanel").getElement().setInnerHTML("");
        RootPanel.get("infoPanel").add(wholePanel);


    }

    public static ArrayList<Audiobook> getUnique(Audiobook[] books){
        ArrayList<Audiobook> purchasedList = new ArrayList<>();

        for(Audiobook audiobook: books){
            boolean inList = false;
            for(Audiobook pb: purchasedList) {
                if (audiobook.getIsbn() == pb.getIsbn()){
                    //in list
                    inList=true;
                }
            }

            if(!inList){
                purchasedList.add(audiobook);
            }
        }
        return purchasedList;
    }

    public static ArrayList<Audiobook> getUnique(ArrayList<Audiobook> books, ArrayList<Audiobook> purchased){
        ArrayList<Audiobook> newList = new ArrayList<>();

        //books: A B C
        //purchased : A

        for(Audiobook audiobook: books){
            boolean inList = false;
            for(Audiobook bk: purchased) {
                if (audiobook.getIsbn() == bk.getIsbn()){
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



    public static void updateAddress(String address) {
        try {
            customerLoggedIn = MyWebApp.customerLoggedIn;
            addService.updateAddressOfCustomer(customerLoggedIn, address, new AsyncCallback<Customer>() {
                @Override
                public void onFailure(Throwable throwable) {
                    Window.alert(throwable.getMessage());
                }

                @Override
                public void onSuccess(Customer customer) {
                    customerLoggedIn = customer;
                    displayCustomerInfo(customerLoggedIn);
                }
            });
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

    }

    public static void addToPurchase(final ArrayList<Audiobook> list) {

        customerLoggedIn = MyWebApp.customerLoggedIn;

        addService.addPurchase(customerLoggedIn, new AsyncCallback<Customer>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Customer customer) {
                customerLoggedIn = customer;
                MyWebApp myWebApp = new MyWebApp();
                MyWebApp.menu.removeFromParent();
                myWebApp.setMenuBar();

                if (MyWebApp.inMyProfilepage) {
                    displayCustomerInfo(customerLoggedIn);
                }
            }
        });


    }

    public static void addContributor(){
        employeeLoggedIn = MyWebApp.employeeLoggedIn;
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setStyleName("dialogVPanel");
        dialogVPanel.setSpacing(10);

        dialogVPanel.add(new HTML("<b>Enter name of contributor:</b>"));
        final TextBox normalText = new TextBox();
        normalText.setText("NewCont");
        normalText.ensureDebugId("cwBasicText-textbox");
        normalText.setFocus(true);
        dialogVPanel.add(normalText);

        dialogVPanel.add(new HTML("<b>Enter bio/description::</b>"));
        final TextBox bioText = new TextBox();
        bioText.setText("New to this world.");
        bioText.ensureDebugId("cwBasicText-password");
        dialogVPanel.add(bioText);

        dialogVPanel.add(new HTML("<b>Enter DOB (format: YYYY-MM-DD)</b>"));
        final TextBox DOB = new TextBox();
        DOB.setText("2010-01-01");
        DOB.ensureDebugId("cwBasicText-textbox");
        dialogVPanel.add(DOB);



        HorizontalPanel buttonPanel = new HorizontalPanel();
        final Button signUpButton = new Button("Add to DB");
        signUpButton.getElement().setId("closeButton");
        buttonPanel.add(signUpButton);

        final Button cancelButton = new Button("Cancel");
        cancelButton.getElement().setId("closeButton");
        buttonPanel.add(cancelButton);


        dialogVPanel.add(buttonPanel);
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setWidget(dialogVPanel);
        dialogBox.center();

        signUpButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                addService.addContributor(normalText.getText(), bioText.getText(), DOB.getText(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("Error!");
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        Window.alert("Success! You can now view the database to see the added contributor.");

                    }
                });
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dialogBox.hide();
            }
        });
    }

    public static void trends() {



        allBooksService.getTrendsBestCustomers(new AsyncCallback<HashMap<String, Integer>>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(HashMap<String, Integer> bestCustomers) {
                createGraph(bestCustomers);
            }
        });

    }

    public static void createGraph(final HashMap <String, Integer> bestCustomers){

        allBooksService.getTrendMostBoughtAudiobooks(new AsyncCallback<HashMap<String, Integer>>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(HashMap<String, Integer> popularAudiobooks) {
                ColumnChartTrends columnChart = new ColumnChartTrends(bestCustomers, popularAudiobooks);
            }
        });


    }
}






