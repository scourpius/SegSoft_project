package src;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ListUsers extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            Authenticator auth = new AuthenticatorImpl();

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");

            out.println("<a href='http://localhost:8080/myApp/main'>");
            out.println("<button>return</button>");
            out.println("</a>");
            
            for (Account a : auth.userList()) {
                out.println("<H2> Username: " + a.getUsername() + " Password: " + a.getPassword() + "</H2>");
            }

            out.println("</BODY>");
            out.println("</HTML>");
    }
}