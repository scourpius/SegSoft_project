import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

import java.io.*;
import java.sql.*;
import java.util.*;

public class TestingServlet2 extends HttpServlet {

    //db url
    static final String DB_URL = "jdbc:sqlite://localhost/TEST.db";

    static int counter = 0;

    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
            String aname = request.getParameter("username");
            String apwd = request.getParameter("password");

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");

            Connection conn = null;

            String sql = "INSERT INTO accounts(username,password) VALUES(?,?)";

        try {
            // Obtain the DataSource from JNDI
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/mydb");
                        
            conn = dataSource.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, aname);
            pstmt.setString(2, apwd);
            pstmt.executeUpdate();
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
        
            out.println("<H1> Welcome </H1>");
            out.println("<H2>"+ aname +"</H2>");
            out.println("<H2>"+ apwd +"</H2>");
            out.println("</BODY>");
            out.println("</HTML>");
    }
}