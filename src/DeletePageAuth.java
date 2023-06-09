package src;

import javax.servlet.*;
import javax.servlet.http.*;

import src.Exceptions.*;
import java.io.*;

public class DeletePageAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    AuthenticatorImpl auth = AuthenticatorImpl.getInstance();
    Account authUser;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int pageID = Integer.parseInt(request.getParameter("pageID"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        try {
            HttpSession session = request.getSession(false);

            if (session != null)
                session.setAttribute("OP", "delete_page");

            authUser = auth.check_authenticated_request(request, response);

            auth.delete_page(pageID);
            response.sendRedirect("/myApp/main");

        } catch(AuthenticationErrorException e){
            out.println("<H1> Account not authenticated </H1>");
        } catch (PermissionDeniedException e){
            out.println("<H1> Permission Denied </H1>");
        } catch (Exception e) {
            out.println("<H1> Page does not exist </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>Return to main page</button>");
        out.println("</a>");

        out.println("<a href='http://localhost:8080/myApp/createPage'>");
        out.println("<button>Try again</button>");
        out.println("</a>");


        out.println("</BODY>");
        out.println("</HTML>");
    }
}