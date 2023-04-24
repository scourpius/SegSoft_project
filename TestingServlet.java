import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class TestingServlet extends HttpServlet {

    //db url
    static final String DB_URL = "jdbc:sqlite://localhost/TEST.db";

    static int counter = 0;

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
        out.println("</BODY>");
        out.println("</HTML>");


        //Database operation example:
        String title = "Database Result";
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType + "<html>\n" +
                "<head><title>" + title + "</title></head>" +
                "<body bgcolor = \"#f0f0f0\">\n" +
                "<h1 align = \"center\">" + title + "</h1>\n");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //open connection
            conn = DriverManager.getConnection(DB_URL);

            //SQL query
            stmt = conn.createStatement();

            //sql query goes here
            String sql = "SELECT id FROM Users";
            rs = stmt.executeQuery(sql);

            //extracting and displaying data from results
            while (rs.next()) {
                int id = rs.getInt("id");
                out.println("ID: " + id + "<br>");
            }
            out.println("</body></html>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}