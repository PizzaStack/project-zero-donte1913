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

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class BankingApp {

    private static int userInput;
    private static final int validRegisterUsername = 2000000; //Near max int value to return in checkUserName
    private static Scanner read;
    private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    private static ArrayList<Customer> employeeList = new ArrayList<Customer>();
    private static ArrayList<Customer> bankAdminList = new ArrayList<Customer>();

    /*
     * Purpose:
     *   Start the applications main menu screen and overall execute
     *
     * */
    public static void main(String[] args) {
        System.out.println("Banking App!\n");

        userInput = mainMenu();

        //User should have either logged in or registered at this line
        //jdbConnector dbConn = new jdbConnector();


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
     *  Display the login menu options for returning users
     *
     *
     * @return void
     * */
    private static void loginMenu(Scanner read) {
        int customerIndex = -2;
        String username = "";
        String password = "";
        String typedPassword = "";
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
                         *  TODO: Search for customer password in the customerList
                         *   if password not typed correctly by 3 tries, system will exit
                         *   if customer not found, user needs to register a new customer account
                         *
                         */
                        System.out.println("Press 1 to go back or please enter your username: ");
                        //while (checkUserName(username) < 0) {
                        if (read.hasNextLine()) {
                            username = read.nextLine();//Get username
                            if (username.equals("1"))
                                mainMenu();
                            //If the customer object at the index is not null get the password of that customer
                            // and compare that to what the user types in
                        }
                        System.out.printf("%d\t%s\t%s\t%s\t%s%n", customerList.get(0).getUserID(),
                                customerList.get(0).getFirstName(),
                                customerList.get(0).getLastName(),
                                customerList.get(0).getUserName(),
                                customerList.get(0).getPassword());

                        System.out.println(username);
                        customerIndex = checkUserName(username);
                        System.out.println(customerIndex);
                                /*
                                if (customerList.get(customerIndex) != null) {
                                    password = customerList.get(customerIndex).getPassword();//Get password
                                    break;
                                }*/


                        // }
/*
                        System.out.println("Hello " + customerList.get(customerIndex).getUserName());
                        for (int j = 3; j < 0; j++) {
                            System.out.println("Please enter your password, " + Integer.toString(j) + "attempt(s)" +
                                    " " +
                                    "until app exits!");
                                if (read.hasNextLine()) {
                                    typedPassword = read.nextLine();
                                    if (typedPassword == password) {
                                        System.out.println(password);
                                        System.out.println("Welcome to BankingApp " + customerList.get(customerIndex)
                                        .getFirstName() + "!");
                                        break;
                                    }
                                }
                        }*/

                        //Customer has almost logged in successfully..
                        //customerMenu();
                        break;
                    case 2:
                        //TODO: Search for employee username in the employee file
                        //System.out.printf("Please enter your employee username: ");
                        //checkEmployeeFile();
                        System.out.println("No employees at this time");
                        System.exit(0);
                        break;
                    case 3:
                        //TODO: Search for bank administrator username in the bank administrator file
                        //System.out.printf("Please enter your bank administrator username: ");
                        //checkBankAdminFile();
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
     *   Display the register menu options when registering users in application for the first time
     *
     *   @return int
     */
    private static int registerMenu(Scanner read) {
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
            } while (checkUserName(username) == 0);

            System.out.printf("Please enter your password: ");
            if (read.hasNextLine()) {
                password = read.nextLine();
                System.out.println(password);
            }

            cu = new Customer(firstName, lastName, username, password);

            customerList.add(cu);

            System.out.printf("Thank you " + "%s" + " you're registered" +
                    " and may now login with your username and password!\n", customerList.get(0).getFirstName());
            for (int i = 0; i < customerList.size(); i++) {
                System.out.printf("%d\t%s\t%s\t%s\t%s%n", customerList.get(i).getUserID(),
                        customerList.get(i).getFirstName(),
                        customerList.get(i).getLastName(),
                        customerList.get(i).getUserName(),
                        customerList.get(i).getPassword());
            }
            loginMenu(read);
        } catch (Exception e) {
            System.out.println("Something went wrong with registering you.");
            System.exit(-1);
        }
        return 0;


    }//end of registerMenu


    /*
     *Purpose:
     *  Checks the customer array list for the username given, if the username
     *   given is found and customer is registering then returns 0, if not found then return a number close to max int.
     *   If the customer is logging in and the username given is found return the customer object to reference the
     *   password with the username, if not found return -1.
     *
     *   @return int
     */
    public static int checkUserName(String username) {
        for (int i = 0; i < customerList.size(); i++) {
            //Customer is registering and the username entered is found in the customerList, use another username
            if (customerList.get(i).getUserName() == username) {
                if (userInput == 2) {
                    System.out.println("User name already taken, please try again!");
                    return 0;
                }
                //Customer is logging in and we have found the username in the customerList, return the index to get
                //the customer object password associated with the username
                else
                    return i;
            }
        }
        //Customer is registering and the username was not found in the customerList
        if (userInput == 2)
            return validRegisterUsername;
            //Customer is logging in and the username was not found in the customerList
        else {
            System.out.println("The username you have entered is incorrect, please try again");
            return -1;
        }
    }//End of checkUserName


}//EoC