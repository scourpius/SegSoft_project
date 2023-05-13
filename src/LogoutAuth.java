package src;

import javax.servlet.*;
import javax.servlet.http.*;
import src.Exceptions.*;
import java.io.*;

public class LogoutAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }
    
    Authenticator auth = AuthenticatorImpl.getInstance();
    Account authUser;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");


        try {
            authUser = auth.check_authenticated_request(request,response);
            auth.logout(authUser);

            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();

            response.sendRedirect("/myApp/login");
            
        } catch (AlreadyAuthenticatedException e){
            out.println("<H1> Username doesn't exist </H1>");
        } catch(AuthenticationErrorException e){
            out.println("<H1> Account not authenticated </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }

        out.println("<a href='http://localhost:8080/myApp/main'>");
        out.println("<button>return to main page</button>");
        out.println("</a>");

        out.println("</BODY>");
        out.println("</HTML>");
    }
}