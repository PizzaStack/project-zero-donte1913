import java.sql.*;

//Class used to connect, read, update, and delete data to and from the ElephantSQL database.
public class jdbConnector {
        public jdbConnector() {
            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (java.lang.ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }

            String url = "baasu.db.elephantsql.com";
            String username = "oqkjkozw";
            String password = "NtMu0QMxKYXRe8WAQoVj3QXSgjBGdJaM";

            try {
                Connection db = DriverManager.getConnection(url, username, password);
                Statement st = db.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Customer");
                while (rs.next()) {
                    //userID
                    System.out.print("Column 1 returned ");
                    System.out.println(rs.getString(2));

                    //first name
                    System.out.print("Column 2 returned ");
                    System.out.println(rs.getString(3));

                    //last name
                    System.out.print("Column 3 returned ");
                    System.out.println(rs.getString(4));

                    //user name
                    System.out.print("Column 4 returned ");
                    System.out.println(rs.getString(5));

                    //password
                    System.out.print("Column 5 returned ");
                    System.out.println(rs.getString(6));
                }
                rs.close();
                st.close();
            }
            catch (java.sql.SQLException e) {
                System.out.println(e.getMessage());
            }
        }

}