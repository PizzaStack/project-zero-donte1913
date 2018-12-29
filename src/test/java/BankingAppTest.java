import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class BankingAppTest {

    @Test
    public void checkCustomerCreation() {
        Customer cust = new Customer("", "", "", "");
    }

    //Create connection to DB and print resultSet
    @Test
    public void connectToDB() {
       jdbConnector connector = new jdbConnector();
        connector.readCustomerTable(connector.url, connector.username, connector.password);
    }

    @Test
    public void testRegistration(){


    }

}//EoC



