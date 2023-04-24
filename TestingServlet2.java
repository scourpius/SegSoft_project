import javax.servlet.*;
import javax.servlet.http.*;
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
        try {
            String aname = request.getParameter("username");
            String apwd = request.getParameter("password");

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");
            out.println("<H1> Welcome </H1>");
            out.println("<H2>"+ aname +"</H2>");
            out.println("<H2>"+ apwd +"</H2>");
            out.println("</BODY>");
            out.println("</HTML>");

        } catch (Exception e) {
            // handle authentication error
        }
}
}