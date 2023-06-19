package src;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class CreatePost extends HttpServlet {
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
        out.println("<h3>Create a post for a user!</h3><br>");
        out.println("<form name='postCreationForm'");
        out.println("action='http://localhost:8080/myApp/createPostAuth' method='GET'>");
        out.println("<label for='page_id'>page_id:</label>");
        out.print("<input type='text' size=35 name='page_id'><br>");
        out.println("<label for='postText'>Content of post:</label>");
        out.print("<input type='text' size=35 name='postText'><br>");
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
