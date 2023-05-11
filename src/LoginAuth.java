package src;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;

public class LoginAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
            String rName = request.getParameter("username");
            String rPwd = request.getParameter("password");

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");

            Connection conn = null;

            String sql = "SELECT password FROM accounts where username = ?";

        try {
            // Obtain the DataSource from JNDI
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/mydb");           
            conn = dataSource.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, rName);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next() || !rs.getString("password").equals(rPwd))
                response.sendRedirect("/myApp/login");
            else
                response.sendRedirect("/myApp/main");
            
            //TODO in the future this will check for encrypted passwords + create session
            
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