import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class TestingServlet extends HttpServlet {

    //Driver name
    static final String JDBC_driver = "com.mysql.jdbc.Driver";
    //db url
    static final String DB_URL = "jdbc:mysql://localhost/TEST";

    //credentials
    static final String USER = "root";
    static final String PASS = "password";


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
        out.println("<H1>The Counter App!</H1>");
        out.println("<H1>Value=" + counter + "</H1>");
        out.print("<form action=\"");
        out.print("Test\" ");
        out.println("method=GET>");
        out.println("<br>");
        out.println("<input type=submit name=increment>");
        out.println("</form>");
        out.println("</BODY>");

        out.println("</HTML>");
        counter++;



        //Database operation example:
        String title = "Database Result";
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType + "<html>\n" +
                "<head><title>" + title + "</title></head>" +
                "<body bgcolor = \"#f0f0f0\">\n" +
                "<h1 align = \"center\">" + title + "</h1>\n");
        try {
            //register drive
            Class.forName(JDBC_driver);

            //open connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //SQL query
            Statement stmt = conn.createStatement();

            //sql query goes here
            String sql = "SELECT id FROM Users";
            ResultSet rs = stmt.executeQuery(sql);

            //extracting and displaying data from results
            while (rs.next()){
                int id = rs.getInt("id");
                out.println("ID: " + id + "<br>");
            }
            out.println("</body></html>");

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}