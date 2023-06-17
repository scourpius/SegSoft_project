package src;

import src.Exceptions.AuthenticationErrorException;
import src.Exceptions.PermissionDeniedException;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ListPages extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    Authenticator auth = AuthenticatorImpl.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        try {
            HttpSession session = request.getSession(false);

            if (session != null)
                session.setAttribute("OP", "access");

            auth.check_authenticated_request(request, response);

            for (Account a : auth.userList()) {
                out.println("<H3> Username: </H3>");
                for (PageObject page : a.getPages())
                    out.println("<p> PageID: " + page.getPageId() + " Page title: " + page.getPageTitle() + " Page associated email: " + page.getEmail() + "<p>");
            }

        } catch (AuthenticationErrorException e) {
            out.println("<H1> User not authenticated </H1>");
        } catch (PermissionDeniedException e) {
            out.println("<H1> Permission denied </H1>");
        } catch (CloneNotSupportedException e) {
            out.println("<H1> ERROR </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return</button>");
        out.println("</a>");

        out.println("</BODY>");
        out.println("</HTML>");
    }
}