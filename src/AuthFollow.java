package src;

import javax.servlet.*;
import javax.servlet.http.*;

import src.Exceptions.*;
import java.io.*;

public class AuthFollow extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    AuthenticatorImpl auth = AuthenticatorImpl.getInstance();
    Account authUser;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int page_id = Integer.parseInt(request.getParameter("page_id"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        try {
            HttpSession session = request.getSession(false);

            if (session != null)
                session.setAttribute("OP", "authorize_follow");

            authUser = auth.check_authenticated_request(request, response);

            for (PageObject page : authUser.getPages())
                if (page.getPageId() == page_id) {
                    out.println("<H1>Pending followers:</H1>");
                    for (PageObject pendingFollower : auth.getPendingFollowers(page_id)) {
                        out.println("<p> PageID: " + pendingFollower.getPageId() + " Page title: " + pendingFollower.getPageTitle() + " Page associated email: " + pendingFollower.getEmail() + "</p>");
                        out.println("<a href='http://localhost:8080/myApp/authorizeFollowAuth'>");
                        out.println("<button>Authorize Follower</button>");
                        out.println("<a href='http://localhost:8080/myApp/authorizeFollowAuth'>");
                        out.println("<button>Deny Follower</button>");
                        out.println("</a>");
                    }
                }
            
        } catch(AuthenticationErrorException e){
            out.println("<H1> Account not authenticated </H1>");
        } catch (PermissionDeniedException e){
            out.println("<H1> Permission Denied </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return to main page</button>");
        out.println("</a>");

        out.println("<a href='http://localhost:8080/myApp/createAccount'>");
        out.println("<button>Try again</button>");
        out.println("</a>");


        out.println("</BODY>");
        out.println("</HTML>");
    }
}