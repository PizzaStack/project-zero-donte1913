import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

//Class used to connect, read, update, and delete data to and from the ElephantSQL database.
public class jdbConnector {
    String url;
    String username;
    String password;
    Connection db;

    //Constructor initializes DB connection string parameters
    public jdbConnector() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        url = "jdbc:postgresql://bankingappdb.ccwa0vat9pxu.us-east-2.rds.amazonaws.com:5432/postgres";
        username = "donte1913";
        password = "Baller210";
        //Create DB connection on new object creation

        try {
            //Try to establish connection with DB
            db = DriverManager.getConnection(url, username, password);
        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    //Selects all customers from Customers table in DB
    public void selectAllFromCustomer(Connection db) {
        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM Customers ORDER BY firstname");
            System.out.printf("FirstName\t\t\tLastName\t\t\tUserName\t\t\tUserPassword%n");

          /*
              index 1 = firstname
              index 2 = lastname
              index 3 = username
              index 4 = password
           */
            while (resultSet.next()) {
                System.out.printf("%s\t\t\t\t%s\t\t\t\t\t%s\t\t\t\t%s%n", resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
            }

            resultSet.close();
            st.close();
        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }
    }//End of selectAllFromCustomer


    /*
     * Purpose:
     *  Checks the Customers table in DB for the username given, if the username given is found and customer is
     *   registering then returns 0 to ask for another username, if not found then return 1 to continue
     *   registration. If the customer is logging in and the username entered in is found it
     *   will handle their login then returns 0, if not found, return -1.
     *
     *   @return int
     */
    public int checkUsername(Connection db, Scanner read, String username) {
        String typedPassword = "";
        String password = "";

        try {
            Statement st = db.createStatement();
            boolean isResultSet = st.execute("SELECT * FROM Customers ORDER BY firstname");
            if (isResultSet) {
                ResultSet resultSet = st.getResultSet();
                while (resultSet.next()) {
                    if (resultSet.getString(3).equals(username)) {
                        //If customer is registering and the username entered is
                        // found in the Customers table, use another username
                        if (BankingApp.getUserInput() == 2) {
                            System.out.println("User name already taken, please try again!");
                            return 0;
                        }
                        //Customer is logging in and we have found the username in the Customers table,
                        //just going to handle rest of customer login here then return
                        else {
                            password = resultSet.getString(4);
                            for (int i = 3; i > 0; i--) {
                                System.out.printf("Please enter your password: ");
                                if (read.hasNextLine()) {
                                    typedPassword = read.nextLine();
                                    if (typedPassword.equals(password)) {
                                        System.out.println("Welcome to BankingApp " + resultSet.getString(1) + "!");
                                        return 0;
                                    } else {
                                        System.out.println("Incorrect password " + i +
                                                " more attempt(s) until app exits!");
                                    }
                                }
                            }
                            System.out.println("Maximum password attempts exceeded, exiting app!");
                            System.exit(0);
                        }
                    }
                }
                resultSet.close();

                //Customer is registering and the username was not found in the customerList, proceed with registration
                if (BankingApp.getUserInput() == 2)
                    return 1;
                    //Customer is logging in and the username was not found in the customerList
                else {
                    System.out.println("The username you have entered is incorrect, please try again");
                    return -1;
                }

            }
            st.close();

        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0; //returns 0 by default

    }//End of checkUsername


    //Inserts the given customer into the db and returns true if successful, false if otherwise.
    public boolean insertCustomer(Customer cu, Connection db) {
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO Customers VALUES ('" + cu.getFirstName() + "'," +
                    "'" + cu.getLastName() + "','" + cu.getUserName() + "','" + cu.getPassword() + "')");
            st.close();
            return true;

        } catch (java.sql.SQLException e) {
            e.getMessage();
            return false;
        }
    }//End of insertCustomer


    /*
        Checks the customers table for all fields that match a single row for the joint account customer and if so
        returns true, if not returns false
    */
    public boolean checkForJointAccountCustomer(Connection db, String jaFirstName, String jaLastName, String jaUsername,
                                                String jaPassword) {
        try {
            Statement st = db.createStatement();
            boolean isResultSet = st.execute("SELECT * FROM Customers ORDER BY firstname");
            if (isResultSet) {
                ResultSet resultSet = st.getResultSet();
                while (resultSet.next()) {
                    if (resultSet.getString(1).equals(jaFirstName) && resultSet.getString(2).equals(jaLastName)
                            && resultSet.getString(3).equals(jaUsername) && resultSet.getString(4).equals(jaPassword))
                        return true;
                }
                resultSet.close();
            } else
                System.out.println("Result set was null while searching for joint account customer..");

            st.close();
        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }//End of checkForJointAccountCustomer


    /*
       Inserts the customer account in the accounts table with a pending status to be approved or
        denied by either an employee or bank admin. Also uses prepareCall STORED PROCEDURE instead of insert query
   */
    public void insertIndividualAccount(Connection db, Account account ) {
        try{
           PreparedStatement ps = db.prepareCall("SELECT insertindividualaccount(?,?,?,?,?,?,?);");
           ps.setDouble(1, account.getAccountBalance());
           //TODO initialize the rest of the params to insert the account
           //ps.setNString();
           /*
            ps.setNString();
            ps.setNString();
            ps.setNString();
            ps.setNString();
            ps.setNString();
            ps.setNString();
            */
        }catch (SQLException e){
            e.getMessage();
        }

    }//End of insertAccountApplication


    public void insertJointAccount(Connection db, Account account){

    }

    public void viewAccountApplications(Connection db, String username){

    }

    public void viewEditOpenAccounts(Connection db, String username){

    }
}//EoC