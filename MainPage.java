import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;

public class MainPage extends HttpServlet {
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
            out.println("<a href='http://localhost:8080/myApp/createAccount'>");
            out.println("<button>Create Account</button>");
            out.println("</a>");
            out.println("</BODY>");
            out.println("</HTML>");
    }
}