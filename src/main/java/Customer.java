import java.io.Serializable;

//Class implements the Serializable interface so that the customer objects can be written
// to a stream
public class Customer {
    private static int index = 0;
    private int userID;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;




    public Customer(String firstName, String lastName, String userName, String password){
        this.userID = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        index++;
    }

    public static int getIndex() { return index;    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}//EoC
