package src;

import src.Exceptions.AuthenticationErrorException;
import src.Exceptions.PermissionDeniedException;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;

public class ShowPage extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    AuthenticatorImpl auth = AuthenticatorImpl.getInstance();

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
                session.setAttribute("OP", "show_page");

            auth.check_authenticated_request(request, response);
            
            List<PostObject> posts = auth.access_posts(page_id);

            for(PostObject post : posts)
                out.println("id: " + post.getPostId() + ", msg: " + post.getPostText() + ", date: " + post.getPostDate());

            out.println("<a href='http://localhost:8080/myApp/authFollow?page_id=" + page_id + "'>");
            out.println("<button>Authorize followers</button>");
            out.println("</a>");

        } catch (AuthenticationErrorException e) {
            out.println("<H1> User not authenticated </H1>");
        } catch (PermissionDeniedException e) {
            out.println("<H1> Permission denied </H1>");
            out.println("<a href='http://localhost:8080/myApp/followAuth?page_id=" + page_id + "'>");
            out.println("<button>Request follow</button>");
            out.println("</a>");
            request.setAttribute("page_id", page_id);
        } catch (Exception e) {
            out.println("<H1> ERROR </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return</button>");
        out.println("</a>");

        out.println("</BODY>");
        out.println("</HTML>");
    }
}