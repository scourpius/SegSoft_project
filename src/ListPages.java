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

    AuthenticatorImpl auth = AuthenticatorImpl.getInstance();

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
                session.setAttribute("OP", "list_pages");

            auth.check_authenticated_request(request, response);

            out.println("<form name='visitPage'");
            out.println("action='http://localhost:8080/myApp/showPage' method='GET'>");
            out.println("<label for='page_id'>page_id:</label>");
            out.print("<input type='text' size=35 name='page_id'><br>");
            out.println("<input type='hidden' value=redirect_url>");
            out.println("<input type='submit' value='Submit'>");
            out.println("</form>");

            for (PageObject page : auth.pageList()){
                out.println("<p> PageID: " + page.getPageId() + " Page title: " + page.getPageTitle() + " Page associated email: " + page.getEmail() + "<p>");
                out.println("<a href='http://localhost:8080/myApp/main'>");
                out.println("<button>Visit Page</button>");
                out.println("</a>");
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