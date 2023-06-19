package src;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class DeletePage extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("<h3>Delete a page for a user!</h3><br>");
        out.println("<form name='pageDeletionForm'");
        out.println("action='http://localhost:8080/myApp/deletePageAuth' method='GET'>");
        out.println("<label for='pageID'>PageID:</label>");
        out.print("<input type='number' size=35 name='pageID'><br>");
        out.println("<input type='hidden' value=redirect_url>");
        out.println("<input type='submit' value='Submit'>");
        out.println("</form><br><br>");
        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return</button>");
        out.println("</a>");
        out.println("</BODY>");
        out.println("</HTML>");
    }
}
