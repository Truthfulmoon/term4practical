/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.serverapp.DBConnection;
import java.sql.*;

/**
 *
 * @author rjbur
 */
public class DBConnection {
        /**
     *
     * @return
     * @throws java.sql.SQLException
     */
public static Connection derbyConnnection()throws SQLException{
        
    
   String URL="jdbc:derby:StudentInformation;create=true";
   String username="root";
   String password="admin";
  
   Connection connection= DriverManager.getConnection(URL,username,password);
   return connection;
   
}
}



   

