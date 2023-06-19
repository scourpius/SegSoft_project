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
            out.println("<h3>List of pages in the system:</h3><br>");

            for (PageObject page : auth.pageList()){
                out.println("<p>ID: " + page.getPageId() + "; Name: '" + page.getPageTitle() + "'; Associated email: '" + page.getEmail() + "'</p>");
                out.println("<a href='http://localhost:8080/myApp/showPage?page_id=" + page.getPageId() + "'>");
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

        out.println("<br><a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return</button>");
        out.println("</a>");

        out.println("</BODY>");
        out.println("</HTML>");
    }
}