package src;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ChangePassword extends HttpServlet {
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
        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return</button>");
        out.println("</a>");
        out.println("<form name='passwordChangeForm'");
        out.println("action='http://localhost:8080/myApp/changePasswordAuth' method='GET'>");
        out.println("<label for='username'>Username:</label>");
        out.print("<input type='text' size=35 name='username'><br>");
        out.println("<label for='password'>Password:</label>");
        out.print("<input type='password' size=35 name='password'><br>");
        out.println("<label for='password2'>Confirmation:</label>");
        out.print("<input type='password' size=35 name='password2'><br>");
        out.println("<input type='hidden' value=redirect_url>");
        out.println("<input type='submit' value='Submit'>");
        out.println("</form>");
        out.println("</BODY>");
        out.println("</HTML>");
    }
}
