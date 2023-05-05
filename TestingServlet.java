import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

import java.io.*;
import java.sql.*;
import java.util.*;

public class TestingServlet extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<form name='loginform'");
        out.println("action='http://localhost:8080/myApp/Test2' method='GET'>");
        out.print("<input type='text' size=35 name='username'>");
        out.print("<input type='password' size=35 name='password'>");
        out.println("<input type='hidden' value=redirect_url>");
        out.println("<input type='submit' value='Submit'>");
        out.println("</form>");

        Connection conn = null;

        String sql = "SELECT * FROM accounts";

        try {
            // Obtain the DataSource from JNDI
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/mydb");
                        
            conn = dataSource.getConnection();

            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            
            // loop through the result set
            while (rs.next()) {
                out.println("<H1>" + rs.getString("username") + "</H1>");
                out.println("<H1>" + rs.getString("password") + "</H1>");
            }
            out.println("<H1>Table created.</H1>");
            
        } catch (SQLException e) {
            out.println("<H1>SQL error.</H1>");
        } catch (NamingException e){
            out.println("<H1>Naming error.</H1>");
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                out.println("<H1>SQL error2.</H1>");
            }
        }

        out.println("</BODY>");
        out.println("</HTML>");
    }
}