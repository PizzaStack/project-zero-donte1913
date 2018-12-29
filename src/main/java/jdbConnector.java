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
            boolean isResultSet = st.execute("SELECT * FROM public.customers ORDER BY firstname");
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
                                        System.out.println("\nWelcome to BankingApp " + resultSet.getString(1) + "!");
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
    public void insertCustomer(Customer cu, Connection db) {
        try {
            Statement st = db.createStatement();
            st.execute("INSERT INTO public.customers VALUES ('" + cu.getFirstName() + "'," +
                    "'" + cu.getLastName() + "','" + cu.getUserName() + "','" + cu.getPassword() + "')");

            st.close();
        } catch (java.sql.SQLException e) {
            e.getMessage();

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
            boolean isResultSet = st.execute("SELECT * FROM public.customers ORDER BY firstname");
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


    //View all account applications for the user logged in the app
    public void viewAccountApplications(Connection db, String username) {
        int i = 0;
        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT accountid, accountbalance, username," +
                    " accounttype, status, username2, \"jointAccount\"\n" +
                    "\tFROM public.testaccounts\n" +
                    "WHERE username = '" + username + "' AND status = 'Account Application Pending' OR\n" +
                    "username2 = '" + username + "' AND status = 'Account Application Pending' OR\n" +
                    "username = '" + username + "' AND status = 'Account Application Denied' OR\n" +
                    "username2 = '" + username + "' AND status = 'Account Application Denied' OR\n" +
                    "username = '" + username + "' AND status = 'Account Application Approved' OR\n" +
                    "username2 = '" + username + "' AND status = 'Account Application Approved';");
            System.out.printf("\nAccount ID\t| Account Balance\t| UserName\t| Account Type\t| Status\t| Username (2)\t| Is Joint Account?%n%n");

            while (resultSet.next()) {
                System.out.printf("%s\t\t\t%s\t\t\t\t\t%s\t\t %s\t %s\t%s\t%s%n", resultSet.getInt(1),
                        resultSet.getFloat(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)
                        , resultSet.getString(6), resultSet.getString(7));
                i++;
            }

            if (i == 0)
                System.out.println("\tNo account applications to show at this time");

            resultSet.close();
            st.close();
        } catch (java.sql.SQLException e) {
            e.getMessage();
        }
    }//End of viewAccountApplications


    /*
       Inserts the customer account in the accounts table with a pending status to be approved or
        denied by either an employee or bank admin. Also uses prepareCall STORED PROCEDURE instead of insert query
   */
    public boolean insertIndividualAccount(Connection db, Account account) {
        Account.AccountType type = account.type;
        String strType = "";

        if (type == Account.AccountType.CHECKING)
            strType = "Checking";
        else
            strType = "Savings";


        try {
            Statement st = db.createStatement();
            boolean isResultSet = st.execute("INSERT INTO public.testaccounts(\n" +
                    "\taccountid, accountbalance, username, accounttype, status, username2, \"jointAccount\")\n" +
                    "\tVALUES (" + account.getAccountID() + ", " + account.getAccountBalance() + ", '" + account.getUsername() + "'," +
                    "'" + strType + "', 'Account Application Pending', '', 'false');");
            if (isResultSet) {
                System.out.println(st.getUpdateCount() + " rows affected");
                st.close();
                return true;
            } else {
                st.close();
                return false;
            }
        } catch (SQLException e) {
            e.getMessage();
            return false;
        }

    }//End of insertIndividualAccount


    //Inserts joint account in testaccounts table
    public boolean insertJointAccount(Connection db, Account account) {
        try {
            Statement st = db.createStatement();
            boolean isResultSet = st.execute("INSERT INTO public.testaccounts(\n" +
                    "\taccountid, accountbalance, username, accounttype, status, username2, \"jointAccount\")\n" +
                    "\tVALUES (" + account.getAccountID() + ", " + account.getAccountBalance() + ", '" + account.getUsername() + "'," +
                    "'Checking', 'Account Application Pending', '" + account.getJaUsername() + "', 'true');");
            if (isResultSet) {
                System.out.println(st.getUpdateCount() + " rows affected");
                st.close();
                return true;
            } else
                st.close();

        } catch (SQLException e) {
            e.getMessage();
            return false;
        }
        return false; //Returns false by default
    }

    /*
        Implements the View/Edit Open Accounts menu for customers
        to withdraw, deposit, and transfer funds between accounts
    */
    public void viewEditOpenAccounts(Connection db, Scanner read, String username) {
        int i = 0;
        int accountID = -1;
        int transferAcc = -1;
        int action = 0;
        double amt = 0;
        double currentBal = 0;
        boolean flag = true;

        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT accountid, accountbalance, username," +
                    " accounttype, status, username2, \"jointAccount\"\n" +
                    "\tFROM public.testaccounts" +
                    "\tWHERE username = '" + username + "' AND status = 'Account Application Approved' OR " +
                    "username2 = '" + username + "' AND status = 'Account Application Approved';");
            System.out.printf("\nAccount ID\t| Account Balance\t| UserName\t| Account Type\t| Status\t| Username (2)\t| Is Joint Account?%n%n");

            while (resultSet.next()) {
                i++;
                System.out.printf("%s\t\t\t%s\t\t\t\t\t%s\t\t %s\t %s\t%s\t%s%n", resultSet.getInt(1),
                        resultSet.getFloat(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)
                        , resultSet.getString(6), resultSet.getString(7));

            }
            if (i == 0) {
                System.out.println("\tNo open accounts to edit at this time.");
                return;
            }

            FLAG1:
            while (flag) {
                System.out.printf("Please enter the account ID you would like to edit or \"<\" to go back to customer menu: ");
                if (!read.hasNextInt() && read.hasNextLine()) {
                    if (read.nextLine().equals("<"))
                        return;
                } else if (read.hasNextInt() && read.hasNextLine()) {
                    accountID = Integer.parseInt(read.nextLine());
                    resultSet = st.executeQuery("SELECT accountid, accountbalance, username," +
                            " accounttype, status, username2, \"jointAccount\"\n" +
                            "\tFROM public.testaccounts" +
                            "\tWHERE username = '" + username + "' AND status = 'Account Application Approved' OR " +
                            "username2 = '" + username + "' AND status = 'Account Application Approved';");


                    while (resultSet.next()) {
                        if (resultSet.getInt(1) == accountID) {
                            break FLAG1;
                        } else {
                            System.out.println("Invalid account ID entry, please try again.");
                            continue FLAG1;
                        }
                    }

                } else {
                    System.out.println("Invalid entry, please try again.");
                    continue FLAG1;
                }
            }//FLAG1

            resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE accountid = " + accountID + ";");

            Account acct = new Account(0.0, username, false, Account.AccountType.CHECKING, "");
            Account transferAccount = new Account(0.0, username, false, Account.AccountType.CHECKING, "");

            while (resultSet.next()) {
                if (resultSet.getInt(1) == accountID) {
                    acct.setAccountID(resultSet.getInt(1));
                    if (resultSet.getString(7).equals("true"))
                        acct.setJointAccount(true);

                    if (resultSet.getString(4).equals("Savings"))
                        acct.setType(Account.AccountType.SAVINGS);

                    if (!resultSet.getString(6).equals("") || resultSet.getString(6) != null)
                        acct.setJaUsername(resultSet.getString(6));
                }
            }



            FLAG2:
            while (flag) {
                resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE accountid = " + accountID + ";");
                //Implements withdraw, deposit, and transfer
                System.out.println("Please enter number to perform matching" +
                        " \"edit account option\" for account " + accountID);
                System.out.println("Edit Account Options:\n" +
                        "1. Withdraw Funds\n2. Deposit Funds\n3. Transfer Funds\n4. Back to customer menu");

                if (read.hasNextInt() && read.hasNextLine()) {
                    action = Integer.parseInt(read.nextLine());
                    switch (action) {
                        case 1:
                            System.out.printf("Please enter the amount you would like to withdraw: ");
                            if (read.hasNextInt() && read.hasNextLine()) {
                                amt = Double.parseDouble(read.nextLine());
                                if (resultSet.next()) {
                                    currentBal = resultSet.getFloat(2);
                                    if (amt > 0 && amt < currentBal) {
                                        System.out.printf("Are you sure you want to withdraw $%f from account %d?\n" +
                                                "1. Yes\n2. No\n", amt, accountID);
                                        if (read.hasNextInt() && read.hasNextLine()) {
                                            int opt = read.nextInt();
                                            switch (opt) {
                                                case 1:
                                                    acct.setAccountBalance(currentBal - amt);
                                                    if (updateAccount(db, acct, transferAccount, 1)) {
                                                        System.out.println("Withdrawal successful!");
                                                        viewEditOpenAccounts(db, read, username);
                                                    } else {
                                                        System.out.println("Could not withdraw funds at this time.");
                                                        viewEditOpenAccounts(db, read, username);
                                                    }
                                                case 2:
                                                    continue FLAG2;
                                                default:
                                                    System.out.println("Invalid account ID entry, please try again.");
                                                    continue FLAG2;
                                            }
                                        }
                                    } else {
                                        System.out.println("Invalid withdraw amount, please try again.");
                                        continue FLAG2;
                                    }
                                }
                            }
                            break;
                        case 2:
                            System.out.print("Please enter the amount you would like to deposit: ");
                            if (read.hasNextInt() && read.hasNextLine()) {
                                amt = Double.parseDouble(read.nextLine());
                                if (resultSet.next()) {
                                    currentBal = resultSet.getFloat(2);
                                    if (amt > 0) {
                                        System.out.printf("Are you sure you want to deposit $%f to account %d?\n" +
                                                "1. Yes\n2. No\n", amt, accountID);
                                        if (read.hasNextInt() && read.hasNextLine()) {
                                            int opt = Integer.parseInt(read.nextLine());
                                            switch (opt) {
                                                case 1:
                                                    acct.setAccountBalance(currentBal + amt);
                                                    if (updateAccount(db, acct, transferAccount, 2)) {
                                                        System.out.println("Deposit successful!");
                                                        viewEditOpenAccounts(db, read, username);
                                                    } else {
                                                        System.out.println("Could not deposit funds at this time.");
                                                        viewEditOpenAccounts(db, read, username);
                                                    }
                                                case 2:
                                                    continue FLAG2;
                                                default:
                                                    System.out.println("Invalid account ID entry, please try again.");
                                                    continue FLAG2;
                                            }
                                        }
                                    } else {
                                        System.out.println("Invalid deposit amount, please try again.");
                                        continue FLAG2;
                                    }
                                }
                            }
                            break;
                        case 3:
                            System.out.print("Please enter the amount you would like to transfer: ");
                            if (read.hasNextInt() && read.hasNextLine()) {
                                amt = Double.parseDouble(read.nextLine());
                                if (resultSet.next()) {
                                    currentBal = resultSet.getFloat(2);
                                    if (amt > 0 && amt < currentBal) {

                                        System.out.print("Please enter the account ID you would like to transfer the funds to: ");
                                        if (read.hasNextInt() && read.hasNextLine()) {
                                            transferAcc = Integer.parseInt(read.nextLine());
                                        }

                                        resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE " +
                                                "accountid = " + transferAcc + ";");
                                        while (resultSet.next()) {
                                            if (resultSet.getInt(1) == transferAcc) {
                                                System.out.printf("Are you sure you want to transfer funds from account " +
                                                        acct.getAccountID() + " to account " + transferAcc + "?\n1. Yes\n2. No\n");
                                                if (read.hasNextInt() && read.hasNextLine()) {
                                                    int opt = read.nextInt();
                                                    switch (opt) {
                                                        case 1:

                                                            while (resultSet.next()) {
                                                                if (resultSet.getInt(1) == transferAcc)
                                                                    transferAccount.setAccountID(resultSet.getInt(1));

                                                                else if (resultSet.getString(7).equals("true"))
                                                                    transferAccount.setJointAccount(true);

                                                                else if (resultSet.getString(4).equals("Savings"))
                                                                    transferAccount.setType(Account.AccountType.SAVINGS);

                                                                else if (!resultSet.getString(6).equals("") && resultSet.getString(6) != null)
                                                                    transferAccount.setJaUsername(resultSet.getString(6));

                                                            }

                                                            acct.setAccountBalance(currentBal - amt);
                                                            currentBal = transferAccount.getAccountBalance();
                                                            transferAccount.setAccountBalance(currentBal + amt);
                                                            if (updateAccount(db, acct, transferAccount, 3)) {
                                                                System.out.println("Transfer successful!");
                                                                viewEditOpenAccounts(db, read, username);
                                                            } else {
                                                                System.out.println("Could not transfer funds at this time.");
                                                                viewEditOpenAccounts(db, read, username);
                                                            }
                                                        case 2:
                                                            continue FLAG2;
                                                        default:
                                                            System.out.println("Invalid account ID entry, please try again.");
                                                            continue FLAG2;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case 4:
                            return;
                        default:
                            System.out.println("Invalid account edit entry, please try again.");
                    }
                }
            }//FLAG2


            resultSet.close();
            st.close();
        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }

    }//End of viewEditOpenAccounts


    /*
       Purpose
        Will return true if the corresponding account was successfully able to be updated or false if otherwise

    @params
        Connection db - DB connection object
        Account account - The account object holding information that is to be updated in the DB
        int action - The action select by the user to either (1) WITHDRAW, (2)DEPOSIT, or (3)TRANSFER
        int amt - The integer amount of how much money the user is trying to correspond with the update action

    @return
        true by default
    */
    public boolean updateAccount(Connection db, Account account, Account transferAccount, int action) {
        boolean isResultSet = true;
        Statement st = null;
        try {
            switch (action) {

                case 1://(1) WITHDRAW
                    st = db.createStatement();
                    st.execute("UPDATE public.testaccounts" +
                            " SET accountbalance = " + account.getAccountBalance() +
                            " WHERE accountid = " + account.getAccountID() + ";");

                    break;
                case 2://(2) DEPOSIT
                    st = db.createStatement();
                    isResultSet = st.execute("UPDATE public.testaccounts" +
                            " SET accountbalance = " + account.getAccountBalance() +
                            " WHERE accountid = " + account.getAccountID() + ";");

                    break;
                case 3://(3) TRANSFER
                    st = db.createStatement();
                    st.execute("UPDATE public.testaccounts" +
                            " SET accountbalance = " + account.getAccountBalance() +
                            " WHERE accountid = " + account.getAccountID() + ";");

                    st.execute("UPDATE public.testaccounts" +
                            " SET accountbalance = " + transferAccount.getAccountBalance() +
                            " WHERE accountid = " + transferAccount.getAccountID() + ";");

                    break;
            }
            st.close();

        } catch (SQLException e) {
            e.getMessage();
        }


        return true; //Returns true by default

    }//End of updateAccount


    //So employees can view customer info
    public void viewCustomerInformation() {
        int i = 0;
        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT public.testcustomer.fn, public.testcustomer.ln, public.testaccounts.accountid," +
                    " public.testaccounts.accountbalance, public.testaccounts.username, public.testaccounts.accounttype, public.testaccounts.status\n" +
                    "FROM public.testaccounts\n" +
                    "INNER JOIN public.testcustomer ON public.testcustomer.usrn = public.testaccounts.username");


            System.out.printf("\nFirst Name\t| Last Name\t| Account ID\t| Account Balance\t| UserName\t| Account Type\t| Status%n%n");

            while (resultSet.next()) {
                System.out.printf("%s\t\t\t%s\t\t\t%s\t\t\t\t%s\t\t\t\t  %s\t  %s\t  %s%n", resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3), resultSet.getDouble(4), resultSet.getString(5)
                        , resultSet.getString(6), resultSet.getString(7));
                i++;
            }

            if (i == 0)
                System.out.println("\tNo customer accounts to view at this time");

            resultSet.close();
            st.close();
        } catch (java.sql.SQLException e) {
            e.getMessage();
        }
    }//End of viewCustomerInformation


    /*Implements the approve or deny menu for employees and bank admins
     *
     * @return boolean
     */
    public void approveDenyOpenApplications(Scanner read) {
        int i = 0;
        int action = -1;
        int tranAccID = -1;
        boolean isResultSet = true;

        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE status = 'Account Application Pending';");

            System.out.printf("\nAccount ID\t| Account Balance\t| UserName\t| Account Type\t| Status\t| Username (2)\t| Is Joint Account?%n%n");

            while (resultSet.next()) {
                i++;
                System.out.printf("%s\t\t\t%s\t\t\t\t\t%s\t\t %s\t  %s\t%s\t%s%n", resultSet.getInt(1),
                        resultSet.getFloat(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)
                        , resultSet.getString(6), resultSet.getString(7));

            }

            if (i == 0) {
                System.out.println("\tNo pending account applications to view at this time");
                return;
            } else {

                TRUE:
                while (true) {
                    System.out.print("Please enter the account ID of the application to be edited or \"<\" to go back to customer menu: : ");
                    if (!read.hasNextInt() && read.hasNextLine()) {
                        if (read.nextLine().equals("<"))
                            return;
                    } else if (read.hasNextInt() && read.hasNextLine()) {
                        tranAccID = Integer.parseInt(read.nextLine());
                    } else {
                        System.out.println("Invalid account entry, please try again");
                        continue TRUE;
                    }

                    resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE " +
                            "accountid = " + tranAccID + " AND status = 'Account Application Pending';");
                    while (resultSet.next()) {
                        if (resultSet.getInt(1) == tranAccID && resultSet.getString(5).equals("Account Application Pending"))
                            break TRUE;
                        else {
                            System.out.println("Invalid account entry, please try again");
                            continue TRUE;
                        }
                    }
                }//TRUE
            }

            System.out.println("Please enter number to approve or deny the account application\n"
                    + "1. Approve\n2. Deny\n3. Back to employee menu");

            if (read.hasNextInt() && read.hasNextLine()) {
                action = Integer.parseInt(read.nextLine());
                switch (action) {
                    case 1:
                        st.executeQuery("UPDATE public.testaccounts" +
                                " SET status = 'Account Application Approved'" +
                                " WHERE accountid = " + tranAccID + ";");
                    case 2:
                        st.executeQuery("UPDATE public.testaccounts" +
                                " SET status = 'Account Application Denied'" +
                                " WHERE accountid = " + tranAccID + ";");
                    case 3:
                        return;
                    default:
                        System.out.println(action + " is not a valid entry, please try again.");
                        approveDenyOpenApplications(read);
                }
            }
            resultSet.close();
            st.close();


        } catch (java.sql.SQLException e) {
            e.getMessage();
        }

    }//End of approveDenyOpenApplications


    //Implements cancelling of open customer accounts for bank admins
    public void cancelOpenAccounts(Scanner read, Connection db) {
        int i = 0;
        int acctID = -1;
        try {
            Statement st = db.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM public.testaccounts;");

            System.out.printf("\nAccount ID\t| Account Balance\t| UserName\t| Account Type\t| Status\t| Username (2)\t| Is Joint Account?%n%n");

            while (resultSet.next()) {
                i++;
                System.out.printf("%s\t\t\t%s\t\t\t\t\t%s\t\t  %s\t  %s\t%s\t%s%n", resultSet.getInt(1),
                        resultSet.getFloat(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)
                        , resultSet.getString(6), resultSet.getString(7));

            }
            if (i == 0) {
                System.out.println("\tNo customer accounts to show at this time");
                return;
            }

            TRUE:
            while (true) {
                System.out.print("Please enter the account ID of the open account that is to be cancelled or \"<\" to go back to admin menu: ");
                if (!read.hasNextInt() && read.hasNextLine()) {
                    if (read.nextLine().equals("<"))
                        return;
                }
                if (read.hasNextInt() && read.hasNextLine())
                    acctID = Integer.parseInt(read.nextLine());

                if (acctID < 0) {
                    System.out.println("Invalid account entry, please try again");
                    continue;
                }
                resultSet = st.executeQuery("SELECT * FROM public.testaccounts WHERE " +
                        "accountid = " + acctID + ";");
                while (resultSet.next()) {
                    if (resultSet.getInt(1) == acctID) {
                        st.execute("DELETE FROM public.testaccounts WHERE accountid = " + acctID);


                    } else {
                        System.out.println("Invalid account entry, please try again");
                        continue TRUE;
                    }
                }
                resultSet.close();
                st.close();
            }//TRUE

        } catch (SQLException e) {
            e.getMessage();
            return;
        }

    }//End of cancelOpenAccounts


}//EoC