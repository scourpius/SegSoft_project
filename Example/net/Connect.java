package Example.net;

import java.sql.*;

public class Connect {
  public static void main( String args[] ) {
      Connection c = null;

      String sql = "SELECT * FROM warehouses";

      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:test.db");

         Statement stmt  = c.createStatement();
         ResultSet rs    = stmt.executeQuery(sql);
        
        // loop through the result set
        while (rs.next()) {
            System.out.println(rs.getString("username") +  "\t" + 
                               rs.getString("password"));
        }

      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }

      System.out.println("Data inserted successfully");
   }
}