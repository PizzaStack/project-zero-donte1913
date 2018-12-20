import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class BankingAppTest {

    @Test
    public void checkCustomerCreation() {
        Customer cust = new Customer("", "", "", "");
    }

    //Write a customer record to the Customers file to register new users
    @Test
    public void connectToDB() {
       jdbConnector connector = new jdbConnector();
    }

    @Test
    public void testRegistration(){


    }

}//EoC



