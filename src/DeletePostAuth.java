package src;

import javax.servlet.*;
import javax.servlet.http.*;

import src.Exceptions.*;
import java.io.*;

public class DeletePostAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    Authenticator auth = AuthenticatorImpl.getInstance();
    Account authUser;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int postID = Integer.parseInt(request.getParameter("postID"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        try {
            HttpSession session = request.getSession(false);

            if (session != null)
                session.setAttribute("OP", "delete_post");

            authUser = auth.check_authenticated_request(request, response);

            auth.delete_post(postID);
            response.sendRedirect("/myApp/main");

        } catch(AuthenticationErrorException e){
            out.println("<H1> Account not authenticated </H1>");
        } catch (PermissionDeniedException e){
            out.println("<H1> Permission Denied </H1>");
        } catch (PostDoesNotExistException e) {
            out.println("<H1> Post does not exist </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
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