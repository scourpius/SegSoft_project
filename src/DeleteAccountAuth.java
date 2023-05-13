package src;

import javax.servlet.*;
import javax.servlet.http.*;
import src.Exceptions.*;
import java.io.*;

public class DeleteAccountAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String rName = request.getParameter("username");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");


        Authenticator auth = new AuthenticatorImpl();

        try {
            auth.delete_account(rName);
            response.sendRedirect("/myApp/main");
            
        } catch (UndefinedAccountException e) {
            out.println("<H1> Account does not exist </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return to main page</button>");
        out.println("</a>");

        out.println("<a href='http://localhost:8080/myApp/deleteAccount'>");
        out.println("<button>Try again</button>");
        out.println("</a>");

        out.println("</BODY>");
        out.println("</HTML>");
    }
}