package com.mycompany.mywebapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.mycompany.mywebapp.shared.Audiobook;
import com.mycompany.mywebapp.shared.Contributor;
import com.mycompany.mywebapp.shared.Customer;
import com.mycompany.mywebapp.shared.Employee;

import java.util.ArrayList;
import java.util.HashMap;




public class MyWebApp implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final AllAudiobooksServiceAsync allBooksService = GWT.create(AllAudiobooksService.class);
    private static final AddToDatabasesAsync addService = GWT.create(AddToDatabases.class);


    static MenuBar cartBar;
    static MenuItem cart;
    static SuggestBox nameField;

    static MenuBar profileBar;
    static MenuItem profile;
    static MenuBar menu;
    static MenuItemSeparator separator4;
    static MenuItemSeparator separator3;
    static MenuItemSeparator separatorEnd;

    static Customer customerLoggedIn = null;
    static Employee employeeLoggedIn = null;
    static boolean loggedInStatus = false;
    static boolean inMyProfilepage = false;

    static Button checkoutButton;


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {


        setMenuBar();


        final Button sendButton = new Button("\uD83D\uDD0E");
        final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();


        allBooksService.getNamesOfAllBooksAuthorsForTitle(new AsyncCallback<String[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getLocalizedMessage());
            }

            @Override
            public void onSuccess(String[] strings) {
                if (strings.length > 0) {
                    for (int i = 0; i < strings.length; ++i) {
                        oracle.add(strings[i]);
                    }
                } else {
                    Window.alert("No books in DB!");
                }

            }
        });


        nameField = new SuggestBox(oracle);


        checkoutButton = new Button("CHECKOUT");
        checkoutButton.setEnabled(false);
        checkoutButton.getElement().setId("checkoutButton");


        sendButton.addStyleName("sendButton");


        RootPanel.get("nameFieldContainer").add(nameField);
        RootPanel.get("sendButtonContainer").add(sendButton);
        RootPanel.get("errorLabelContainer").add(checkoutButton);

        nameField.setFocus(true);

        Element paraHead = DOM.getElementById("infoPanelMessageH3");
        paraHead.setInnerHTML("LISTEN. EXPLORE.");
        Element para = DOM.getElementById("infoPanelMessageP");
        para.setInnerHTML("Welcome to BookStream! We offer a vast range of audiobooks for purchase and streaming. Browse to explore.");

        Listing listing = new Listing();
        Listing.getViewPanelByPopularity();


        //Add a handler to close the DialogBox


        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {

            public void onClick(ClickEvent event) {
                sendNameToServer();
            }

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendNameToServer();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void sendNameToServer() {

                final String textToServer = nameField.getText();
                final String nullMsg = "Sorry, no searches match what you're looking for.\nYou can contact customer service or " +
                        "search for something else.";

                allBooksService.getAllBooks(textToServer, new AsyncCallback<Audiobook[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(Audiobook[] audiobooks) {
                        if (audiobooks.length > 0) {
                            Panel panel = new VerticalPanel();
                            for (int i = 0; i < audiobooks.length; i++) {
                                Panel newGrid = Listing.displayInfoForClickedBook(audiobooks[i]);
                                panel.add(newGrid);
                            }
                            RootPanel.get("infoPanel").getElement().setInnerHTML("");
                            RootPanel.get("infoPanel").add(panel);


                        } else {
                            Window.alert(nullMsg);
                        }
                    }
                });

            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        nameField.addKeyUpHandler(handler);
    }


    public void setMenuBar(){

        HorizontalPanel cwMenuBar = new HorizontalPanel();
        cwMenuBar.addStyleName("cwMenuBar");

        menu = new MenuBar();
        menu.setAutoOpen(true);
        menu.setWidth("900px");
        menu.setAnimationEnabled(true);


        menu.removeStyleName("gwt-MenuBar-horizontal");
        menu.addStyleName("audiomenu");


        class MyAudiobookCommand implements Command {

            private final String item;

            public MyAudiobookCommand(String item) {
                this.item = item;
            }

            @Override
            public void execute() {

                allBooksService.getAllBooks(item, new AsyncCallback<Audiobook[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Audiobook[] audiobooks) {
                        try {

                            Panel panel = new VerticalPanel();
                            for (int i = 0; i < audiobooks.length; i++) {
                                Panel newGrid = Listing.displayInfoForClickedBook(audiobooks[i]);
                                panel.add(newGrid);
                            }

                            RootPanel.get("infoPanel").getElement().setInnerHTML("");
                            RootPanel.get("infoPanel").add(panel);
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                        }


                    }
                });

            }
        }

        class MyAuthorCommand implements Command {

            private final String item;

            public MyAuthorCommand(String item) {
                this.item = item;
            }

            @Override
            public void execute() {

                allBooksService.getAllByAuthor(item, new AsyncCallback<HashMap<Contributor, Audiobook[]>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(HashMap<Contributor, Audiobook[]> contributorHashMap) {
                        Panel newPanel = Listing.getViewsForSearches(contributorHashMap);
                        RootPanel.get("infoPanel").getElement().setInnerHTML("");
                        RootPanel.get("infoPanel").add(newPanel);
                    }
                });

            }
        }


        // Create the edit menu
        MenuBar audiobookBar = new MenuBar(true);
        MenuItem audioItem = new MenuItem("AUDIOBOOK", audiobookBar);

        menu.addItem(audioItem);
        String[] audioOptions = {"TITLE", "POPULAR", "PRICE(L-H)", "RATING"};
        for (int i = 0; i < audioOptions.length; i++) {
            audiobookBar.addItem(audioOptions[i], new MyAudiobookCommand(audioOptions[i]));
        }

        MenuItemSeparator separator = new MenuItemSeparator();
        separator.removeStyleName("gwt-MenuItem-Separator");
        separator.addStyleName("gwt-MenuItem-Separator");
        menu.addSeparator(separator);


        MenuBar authorBar = new MenuBar(true);
        MenuItem audioItem2 = new MenuItem("AUTHORS", authorBar);
        menu.addItem(audioItem2);
        String[] audioOptions2 = {"ALL", "POPULAR"};
        for (int i = 0; i < audioOptions2.length; i++) {
            authorBar.addItem(audioOptions2[i], new MyAuthorCommand(audioOptions2[i]));
        }


        MenuItemSeparator separator2 = new MenuItemSeparator();
        separator2.removeStyleName("gwt-MenuItem-Separator");
        separator2.addStyleName("gwt-MenuItem-Separator");
        menu.addSeparator(separator2);


        final MenuBar genreBar = new MenuBar(true);
        final MenuItem genreItem = new MenuItem("GENRES", genreBar);
        menu.addItem(genreItem);

        allBooksService.getAllGenres(new AsyncCallback<String[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(String[] strings) {
                for (int i = 0; i < strings.length; i++) {
                    genreBar.addItem(strings[i], new MyAudiobookCommand("GENRE " + strings[i]));
                }
            }
        });


        separator3 = new MenuItemSeparator();
        separator3.removeStyleName("gwt-MenuItem-Separator");
        separator3.addStyleName("gwt-MenuItem-Separator");
        menu.addSeparator(separator3);


        cartBar = new MenuBar(true);
        cart = new MenuItem("MY CART", cartBar);
        menu.addItem(cart);

        separatorEnd = new MenuItemSeparator();
        separatorEnd.removeStyleName("gwt-MenuItem-Separator");
        separatorEnd.addStyleName("gwt-MenuItem-Separator");
        menu.addSeparator(separatorEnd);

        addToProfileBar(false);

        menu.ensureDebugId("cwMenuBar");
        cwMenuBar.add(menu);
        RootPanel.get("cwMenuBar").add(cwMenuBar);

    }


    public Panel getSignUpDialogBoxPanel(final DialogBox dialogBox, boolean tryingAgain) {
        final VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setStyleName("dialogVPanel");
        dialogVPanel.setSpacing(10);

        dialogVPanel.add(new HTML("<b>Enter email:</b>"));
        final TextBox normalText = new TextBox();
        normalText.setText("mt@gmail.com");
        normalText.ensureDebugId("cwBasicText-textbox");
        normalText.setFocus(true);
        dialogVPanel.add(normalText);

        dialogVPanel.add(new HTML("<b>Enter password:</b>"));
        final PasswordTextBox normalPassword = new PasswordTextBox();
        normalPassword.setText("abc");
        normalPassword.ensureDebugId("cwBasicText-password");
        dialogVPanel.add(normalPassword);

        dialogVPanel.add(new HTML("<b>Enter name:</b>"));
        final TextBox nameText = new TextBox();
        nameText.setText("Marcus telford");
        nameText.ensureDebugId("cwBasicText-textbox");
        dialogVPanel.add(nameText);

        dialogVPanel.add(new HTML("<b>Enter address:</b>"));
        final TextBox addressText = new TextBox();
        addressText.setText("london");
        addressText.ensureDebugId("cwBasicText-textbox");
        dialogVPanel.add(addressText);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        final Button signUpButton = new Button("Sign Up");
        signUpButton.getElement().setId("closeButton");
        buttonPanel.add(signUpButton);

        final Button cancelButton = new Button("Close");
        cancelButton.getElement().setId("closeButton");
        buttonPanel.add(cancelButton);

        final Button tryAgain = new Button("Try Again");
        tryAgain.getElement().setId("closeButton");

        if (tryingAgain) {
            tryAgain.setVisible(true);
        } else {
            tryAgain.setVisible(false);
        }
        buttonPanel.add(tryAgain);


        dialogVPanel.add(buttonPanel);

        signUpButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addToCustomerBase(normalText.getText(), normalPassword.getText(), nameText.getText(), addressText.getText());
                dialogBox.hide();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dialogBox.hide();
            }
        });

        tryAgain.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dialogBox.hide();
                DialogBox newDialogBox = new DialogBox();
                newDialogBox.setAnimationEnabled(true);
                newDialogBox.setStyleName("gwt-DialogBox");

                final Panel newPanel = getLogInPanel(newDialogBox);
                newDialogBox.add(newPanel);

                newDialogBox.setText("TRY AGAIN");
                newDialogBox.center();
            }
        });

        return dialogVPanel;
    }

    public Panel getLogInPanel(final DialogBox dialogBox) {
        dialogBox.setStyleName("gwt-DialogBox");
        dialogBox.setText("LOG IN");
        dialogBox.setAnimationEnabled(true);

        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setStyleName("dialogVPanel");
        dialogVPanel.setSpacing(20);

        dialogVPanel.add(new HTML("<b>Enter username:</b>"));
        final TextBox normalText = new TextBox();
        normalText.setText("ah@mail.com");
        normalText.ensureDebugId("cwBasicText-textbox");
        dialogVPanel.add(normalText);

        dialogVPanel.add(new HTML("<b>Enter password:</b>"));
        final PasswordTextBox normalPassword = new PasswordTextBox();
        normalPassword.setText("abc");
        normalPassword.ensureDebugId("cwBasicText-password");
        dialogVPanel.add(normalPassword);


        final Button logInButton = new Button("Log In");
        logInButton.getElement().setId("closeButton");
        dialogVPanel.add(logInButton);
        logInButton.setFocus(true);

        final Button cancelButton = new Button("Cancel");
        cancelButton.getElement().setId("closeButton");
        dialogVPanel.add(cancelButton);

        logInButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                validate(dialogBox, normalText.getText().trim(), normalPassword.getText().trim());
            }
        });

        logInButton.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                validate(dialogBox, normalText.getText().trim(), normalPassword.getText().trim());
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dialogBox.hide();
            }
        });

        nameField.setFocus(true);

        return dialogVPanel;

    }

    public void validate(final DialogBox box, String email, String password) {
        //allBooksService.validateCustome

        final String email2 = email;
        final String password2 = password;

        allBooksService.validateCustomer(email, password, new AsyncCallback<Customer>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Customer customer2) {
                if (customer2 != null) {
                    box.hide();
                    customerLoggedIn = customer2;
                    loggedInStatus = true;
                    addToProfileBar(true);


                    int len = customerLoggedIn.getAudio_ISBNs().size();
                    final ArrayList<Audiobook> audiobooks = customerLoggedIn.getAudio_ISBNs();

                    if (customerLoggedIn.getAudio_ISBNs().size() > 0) {
                        checkoutButton.setEnabled(true);

//                        MyWebApp.checkoutButton.addClickHandler(new ClickHandler() {
//                            @Override
//                            public void onClick(ClickEvent clickEvent) {
//                                Listing listing = new Listing();
//                                listing.addToPurchase(audiobooks);
//                            }
//                        });


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

                        MyWebApp.cart.setText("MY CART(" + len + ")");


                        for (Audiobook itemToAdd : audiobooks) {
                            MyWebApp.cartBar.addItem(itemToAdd.getAudioTitle(), new MyCartCommand(itemToAdd));
                        }


                    }


                    //String[] profileOptions = addToProfileBar();
                    // for (int i = 0; i < profileOptions.length; i++) {
                    //profileBar.addItem(profileOptions[i], new MyProfileCommand(profileOptions[i]));

                    //}

                } else {
                    box.hide();
                    allBooksService.validateEmployee(email2, password2, new AsyncCallback<Employee>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert(throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(Employee employee) {
                            if (employee != null) {
                                box.hide();
                                employeeLoggedIn = employee;
                                loggedInStatus = true;
                                addToProfileBar(true);

                            } else {
                                final DialogBox signUpDialogBox = new DialogBox();
                                signUpDialogBox.setAnimationEnabled(true);
                                signUpDialogBox.setStyleName("gwt-DialogBox");
                                signUpDialogBox.setText("TRY AGAIN");

                                final VerticalPanel mainPanel = new VerticalPanel();

                                final Panel signUpDialogBoxPanel = getSignUpDialogBoxPanel(signUpDialogBox, true);


                                HorizontalPanel panel = new HorizontalPanel();
                                panel.add(new HTML("<b style=\"color:red;\">Sorry, you're not in our records.</b>"));

                                mainPanel.add(panel);
                                mainPanel.add(signUpDialogBoxPanel);
                                signUpDialogBox.add(mainPanel);
                                signUpDialogBox.center();
                            }
                        }
                    });
                }
            }

        });
    }

    public void addToCustomerBase(String email, String password, String name, String address) {
        //add customer to DB
        addService.addCustomer(name, email, password, address, new AsyncCallback<Customer>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(throwable.getMessage());
            }

            @Override
            public void onSuccess(Customer customer) {
                customerLoggedIn = customer;
                loggedInStatus = true;
                //setMenuBar();
                addToProfileBar(true);


            }
        });


    }

    public void addToProfileBar(boolean change) {

        if (change) {
            profileBar.removeFromParent();
            menu.removeItem(profile);
            menu.removeSeparator(separator4);
        }


        profileBar = new MenuBar(true);

        if (loggedInStatus && customerLoggedIn!=null) {
            profile = new MenuItem("WELCOME, " + customerLoggedIn.getName(), profileBar);
        }
        else if(loggedInStatus && employeeLoggedIn!=null){
            profile = new MenuItem("WELCOME, " + employeeLoggedIn.getName(), profileBar);
        }


        else {
            profile = new MenuItem("MY PROFILE", profileBar);
        }
        menu.addItem(profile);

        String[] profileOptions;
        if (loggedInStatus && customerLoggedIn!=null) {
            profileOptions = new String[]{"MY ACCOUNT", "LOG OUT"};
        }
        else if(loggedInStatus && employeeLoggedIn!=null){
            profileOptions = new String[]{"ADD CONTRIBUTOR", "VIEW TRENDS", "LOG OUT"};
            cartBar.removeFromParent();
            menu.removeItem(cart);
            menu.removeSeparator(separatorEnd);
            menu.removeSeparator(separator4);
        }

        else {
            profileOptions = new String[]{"LOG IN", "SIGN UP"};
        }

        for (int i = 0; i < profileOptions.length; i++) {
            profileBar.addItem(profileOptions[i], new MyProfileCommand(profileOptions[i]));
        }

        if (change) {
            separator4 = new MenuItemSeparator();
            separator4.removeStyleName("gwt-MenuItem-Separator");
            separator4.addStyleName("gwt-MenuItem-Separator");
            menu.addSeparator(separator4);
        }
    }


    public class MyProfileCommand implements Command {
        private final String item;

        public MyProfileCommand(String item) {
            this.item = item;
        }

        @Override
        public void execute() {
            if (item.equalsIgnoreCase("LOG IN")) {

                DialogBox dialogBox = new DialogBox();
                Panel dialogVPanel = getLogInPanel(dialogBox);
                dialogBox.setWidget(dialogVPanel);
                dialogBox.center();



            } else if (item.equalsIgnoreCase("SIGN UP")) {
                final DialogBox dialogBox = new DialogBox();
                dialogBox.setStyleName("gwt-DialogBox");
                dialogBox.setText("SIGN UP");
                dialogBox.setAnimationEnabled(true);

                Panel dialogPanel = getSignUpDialogBoxPanel(dialogBox, false);
                dialogBox.add(dialogPanel);

                dialogBox.center();
            }

             else if(item.equalsIgnoreCase("LOG OUT")){

                loggedInStatus = false;
                customerLoggedIn = null;
                menu.removeFromParent();
                setMenuBar();
                checkoutButton.setEnabled(false);
                Listing listing1 = new Listing();
                listing1.init();
                Listing.getViewPanelByPopularity();


                // addToProfileBar(true);

                if(inMyProfilepage){
                    Listing listing = new Listing();
                    listing.init();
                    Listing.getViewPanelByPopularity();
                    inMyProfilepage = false;
                }


            }


            else if (item.equalsIgnoreCase("MY ACCOUNT")) {
                Listing listing = new Listing();
                listing.init();

                listing.displayCustomerInfo(customerLoggedIn);

            }

            else if(item.equalsIgnoreCase("ADD CONTRIBUTOR")){
                Listing.addContributor();
            }
            else if(item.equalsIgnoreCase("VIEW TRENDS")){
                Listing listing = new Listing();
                listing.init();

                Listing.trends();
            }
        }
//
//  public static void setCartToDef(){
//    cartBar.removeFromParent();
//    cart.getSubMenu().removeFromParent();
//    cartBar.getElement().removeFromParent();
//    menu.removeItem(cart);
//
//
//    menu.removeSeparator(separator3);
//    menu.removeSeparator(separator4);
//
//    cartBar = new MenuBar(true);
//    cart = new MenuItem("MY CART", cartBar);
//    menu.addItem(cart);
//
//
//
//
//    separator4 = new MenuItemSeparator();
//    separator4.removeStyleName("gwt-MenuItem-Separator");
//    separator4.addStyleName("gwt-MenuItem-Separator");
//    menu.addSeparator(separator3);
//    menu.addSeparator(separator4);
//  }
//


    }
}
