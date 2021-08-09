
package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionUtil {
    //Connection conn = null;
    public static Connection conDB()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppinglistdb?serverTimezone=UTC", "root", "root");
            return myConn;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("ConnectionUtil : "+ex.getMessage());
           return null;
        }
    }
   
}