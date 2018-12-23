/* TODO:
    Display the list of options to start the banking with the app. e.g.
    Login, registration if new customer, menu options that will display for different user types

Project 0: Banking App
Part 1
Description
Requirements
Build the application using Java 8
All interaction with the user should be done through the console using the Scanner class
Customers of the bank should be able to register with a username and password, and apply to open an account.
Customers should be able to apply for joint accounts
Once the account is open, customers should be able to withdraw, deposit, and transfer funds between accounts
All basic validation should be done, such as trying to input negative amounts, overdrawing from accounts etc.

Employees of the bank should be able to view all of their customers information. This includes:
    Account information
    Account balances
    Personal information
Employees should be able to approve/deny open applications for accounts


Bank admins should be able to view and edit all accounts, this includes:
    Approving/denying accounts
    Withdrawing, depositing, transferring from all accounts
    Canceling accounts

All information should be persisted using text files and serialization
100% test coverage is expected using JUnit
You should be using TDD
Logging should be accomplished using Log4J
All transactions should be logged

Part 2
Requirements

Create an SQL script that will create a user in an SQL database and a table schema for storing your bank users and
account information.
Your database should include at least 1 stored procedure.
Have your bank application connect to your SQL database using JDBC and store all information that way.'
You should use the DAO design pattern for data connectivity.
*/

//import java.io.*;

import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.NoSuchElementException;
import java.util.Scanner;


public class BankingApp {

    private static int userInput;
    private static Scanner read;
    //private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    //private static ArrayList<Customer> employeeList = new ArrayList<Customer>();
    //private static ArrayList<Customer> bankAdminList = new ArrayList<Customer>();
    private static jdbConnector connector;

    /*
     * Purpose:
     *   Start the applications main menu screen and overall execute
     *
     * */
    public static void main(String[] args) {
        //System.out.println("Banking App!\n");

        //Connect to AWS DB
        connector = new jdbConnector();
        userInput = mainMenu();

        //User should have either logged in or registered at this line

        try {
            connector.db.close();
            read.close();
        } catch (SQLException e) {
            e.getMessage();
        }
    }//end of main


    /*
     * Purpose:
     *   Display main menu options when first logging in application to login as a return user
     *   or to register as a new customer
     *
     * @return int
     */
    private static int mainMenu() {
        System.out.printf("Type the number for the action, then click enter.\n" +
                "1. Login\n" +
                "2. Register new account\n" +
                "3. Exit\n");
        try {
            read = new Scanner(System.in);

            //Check if user enters a number then presses enter
            if (read.hasNextInt() && read.hasNextLine()) {
                userInput = Integer.parseInt(read.nextLine());
                switch (userInput) {
                    case 1:
                        loginMenu(read);
                        break;
                    case 2:
                        registerMenu(read);
                        break;
                    case 3:
                        System.out.println("Goodbye");
                        System.exit(0);
                    default:
                        System.out.println(userInput + " is not a valid entry, please try again.");
                        mainMenu();
                }
            } else {
                System.out.println(read.nextLine() + " is not a number, please try again.");
                mainMenu();
            }
        } catch (NumberFormatException e) {
            System.out.println("No spaces are needed, type either 1 to login " +
                    "to the banking app or 2 to register as new customer, then press enter, or 3 to exit\n");
            mainMenu();
        } catch (Exception e) {
            System.err.println("ERROR: Something went really wrong!");
            e.printStackTrace();
        }
        return userInput;

    }//end of mainMenu


    /*
     * Purpose:
     *   Display the register menu options when registering users in application for the first time
     *
     *   @return int
     */
    private static void registerMenu(Scanner read) {
        Customer cu = null;
        String firstName = "";
        String lastName = "";
        String username = "";
        String password = "";

        try {
            System.out.printf("Please enter your first name: ");
            if (read.hasNextLine()) {
                firstName = read.nextLine();
                System.out.println(firstName);
            }

            System.out.printf("Please enter your last name: ");
            if (read.hasNextLine()) {
                lastName = read.nextLine();
                System.out.println(lastName);
            }

            //Checks if the username the user enters is already in the customer array list
            //here if so catch exception
            do {
                System.out.printf("Please enter your username: ");

                if (read.hasNextLine())
                    username = read.nextLine();
            } while (connector.checkUsername(connector.db, read, username) == 0);

            System.out.printf("Please enter your password: ");
            if (read.hasNextLine()) {
                password = read.nextLine();
                System.out.println(password);
            }

            cu = new Customer(firstName, lastName, username, password);


            //add the customer to the Customers table
            if (connector.insertCustomer(cu, connector.db))
                System.out.printf("Thank you " + "%s" + ", you're registered" +
                        " and may now login with your username and password!\n", cu.getFirstName());
            else {
                System.out.println("There was an issue inserting you into the DB, returning to main menu.");
                mainMenu();
            }
            loginMenu(read);
        } catch (Exception e) {
            System.out.println("Something went wrong with registering you.");
            System.exit(-1);
        }

    }//end of registerMenu


    /*
     * Purpose:
     *  Display the login menu options for returning users
     *
     *
     * @return void
     * */
    private static void loginMenu(Scanner read) {
        String username = "";
        //Customer c1 = new Customer("", "", "", "");  //??

        System.out.println("Which type of user would you like to log in as?\n" +
                "1. Customer\n" +
                "2. Employee\n" +
                "3. Bank Administrator\n" +
                "4. Exit");
        try {
            //Check if user enters a number then presses enter
            if (read.hasNextInt() && read.hasNextLine()) {
                userInput = Integer.parseInt(read.nextLine());
                switch (userInput) {
                    case 1:
                        /*
                         * Searches for the customer username in the Customers table
                         *   if password not typed correctly by 3 tries, system will exit
                         */
                        do {
                            System.out.println("Press 1 to go back or please enter your username: ");
                            if (read.hasNextLine()) {
                                username = read.nextLine();//Get username
                                if (username.equals("1"))
                                    mainMenu();
                            }
                        } while (connector.checkUsername(connector.db, read, username) == -1);

                        //Customer has logged in successfully!
                        customerMenu(read, username);
                        break;
                    case 2:
                        //TODO: Search for employee username in the employee file
                        //System.out.printf("Please enter your employee username: ");

                        System.out.println("No employees at this time");
                        System.exit(0);
                        break;
                    case 3:
                        //TODO: Search for bank administrator username in the bank administrator file
                        //System.out.printf("Please enter your bank administrator username: ");

                        System.out.println("No bank admins at this time");
                        System.exit(0);
                        break;
                    case 4:
                        System.out.println("Goodbye");
                        System.exit(0);

                    default:
                        System.out.println(userInput + " is not a valid entry, please try again.");
                        loginMenu(read);
                }

            }
        } catch (NumberFormatException e) {
            System.out.println("No spaces are needed.\n\nOptions:\n" +
                    "Press 1 to login as a customer, then press enter.\n" +
                    "Press 2 to login as an employee, then press enter.\n" +
                    "Press 3 to login as a bank administrator, then press enter.\n" +
                    "Press 4 to exit, then press enter.\n");
            loginMenu(read);
        } catch (Exception e) {
            System.err.println("ERROR: Something went really wrong!");
            e.printStackTrace();
        }

    }//end of loginMenu


    /*
     * Purpose:
     *  Display the customer menu options for customers that have registered already with the app
     *
     * @return void
     * */
    private static void customerMenu(Scanner read, String username) {
        //ja = joint account
        String jaFirstName = "";
        String jaLastName = "";
        String jaUsername = "";
        String jaPassword = "";

        System.out.printf("Please enter the number to perform the matching action:\n" +
                "1. Apply for a new account\n" +
                "2. View Account Applications\n" +
                "3. View/Edit Open Accounts\n" +
                "4. Logout\n");
        try {

            if (read.hasNextInt() && read.hasNextLine()) {
                userInput = Integer.parseInt(read.nextLine());
                //Customer menu option selection
                switch (userInput) {
                    case 1:
                        System.out.println("Joint account policy:\nCustomers may only apply for one joint" +
                                " checking account with one other customer.");
                        System.out.println("Which type of account would you like to apply for?\n"
                                + "1. Checking\n" +
                                "2. Savings");

                        if (read.hasNextInt() && read.hasNextLine()) {
                            userInput = Integer.parseInt(read.nextLine());
                            //Checking or Savings account type option selection
                            switch (userInput) {
                                case 1:
                                    System.out.println("Will this account be joint with another customer?\n"
                                            + "1. Yes\n2. No");

                                    if (read.hasNextInt() && read.hasNextLine()) {
                                        userInput = Integer.parseInt(read.nextLine());

                                        switch (userInput) {
                                            //Customer has selected to apply for joint checking account
                                            case 1:
                                                System.out.printf("Please enter the joint account customer's first " +
                                                        "name: ");
                                                if (read.hasNextLine()) {
                                                    jaFirstName = read.nextLine();
                                                    System.out.println(jaFirstName);
                                                }

                                                System.out.printf("Please enter the joint account customer's last " +
                                                        "name: ");
                                                if (read.hasNextLine()) {
                                                    jaLastName = read.nextLine();
                                                    System.out.println(jaLastName);
                                                }

                                                System.out.printf("Please enter the joint account customer's user " +
                                                        "name: ");
                                                if (read.hasNextLine()) {
                                                    jaUsername = read.nextLine();
                                                    System.out.println(jaUsername);
                                                }

                                                System.out.printf("Please enter the joint account customer's " +
                                                        "password: ");
                                                if (read.hasNextLine()) {
                                                    jaPassword = read.nextLine();
                                                    System.out.println(jaPassword);
                                                }
                                                //Checks for joint account customer
                                                if (connector.checkForJointAccountCustomer(connector.db, jaFirstName,
                                                        jaLastName,
                                                        jaUsername, jaPassword)) {
                                                    System.out.println("Sending joint checking account application " +
                                                            "with " + jaUsername + " for approval. " +
                                                            "You may now monitor the approval status in the \"View " +
                                                            "Account Applications\" menu.");

                                                    Account account = new Account(0.0, username, true,
                                                            Account.AccountType.CHECKING, jaUsername);

                                                    connector.insertJointAccount(connector.db, account);
                                                } else
                                                    System.out.println("Could not find customer for joint account" +
                                                            " application, returning to customer menu.");

                                                customerMenu(read, username);

                                            //Individual checking account application option selection
                                            case 2:
                                                System.out.println("Sending individual checking account application for approval." +
                                                        " You may now monitor the approval status in the \"View " +
                                                        "Account Applications\" menu.");
                                                Account account = new Account(0.0, username, false,
                                                        Account.AccountType.CHECKING,"");

                                                connector.insertIndividualAccount(connector.db, account);
                                                customerMenu(read, username);
                                            default:
                                                System.out.println(userInput +" is not a valid entry, going back to " +
                                                        "customer menu..");
                                                customerMenu(read, username);
                                        }
                                    }

                                //Individual savings account application option selection
                                case 2:
                                    System.out.println("Sending individual savings account application for approval." +
                                            " You may now monitor the approval status in the \"View " +
                                            "Account Applications\" menu.");
                                    Account account = new Account(0.0, username, false,
                                            Account.AccountType.SAVINGS,"");

                                    connector.insertIndividualAccount(connector.db, account);
                                    customerMenu(read, username);
                                default:
                                    System.out.println(userInput +" is not a valid entry, going back to " +
                                            "customer menu..");
                                    customerMenu(read, username);
                            }
                        }
                    case 2:
                        //View Account Applications
                        //TODO view app. menu for customers
                        connector.viewAccountApplications(connector.db, username);
                        break;
                    case 3:
                        //View/Edit Open Accounts
                        //TODO view edit open accouts  menu for customers
                        //  also implement withdraw, deposit, and transfering funds in this menu
                        connector.viewEditOpenAccounts(connector.db, username);
                        System.exit(0);
                    case 4:
                        System.out.println("Goodbye");
                        System.exit(0);
                    default:
                        System.out.println(userInput + " is not a valid entry, please try again.");
                        customerMenu(read, username);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("No spaces are needed.\n\nOptions:\n" +
                    "Press 1 to apply for a new account, then press enter.\n" +
                    "Press 2 to view your current account applications, then press enter.\n" +
                    "Press 3 to view and edit your open accounts, then press enter.\n" +
                    "Press 4 to logout, then press enter.\n");
            loginMenu(read);
        } catch (Exception e) {
            System.err.println("ERROR: Something went really wrong!");
            e.printStackTrace();
        }


    }//End of customerMenu




    public static void employeeMenu(Scanner read, String username){

    }//End of employee menu



    public static void bankAdminMenu(Scanner read, String username){

    }//End of bank admin menu







}//EoC