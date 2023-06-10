package src;

import javax.servlet.*;
import javax.servlet.http.*;

import src.Exceptions.*;
import java.io.*;

public class LoginAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    Authenticator auth = AuthenticatorImpl.getInstance();
    Account authUser;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String rName = request.getParameter("username");
        String rPwd = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        try {
            authUser = auth.authenticate_user(rName, rPwd);

            HttpSession session = request.getSession(true);
            session.setAttribute("USER", authUser.getUsername());
            session.setAttribute("PWD", authUser.getPassword());

            response.sendRedirect("/myApp/main");
            
        } catch (LockedAccountException e) {
            out.println("<H1> Account locked </H1>");
        } catch (AuthenticationErrorException | UndefinedAccountException e){
            out.println("<H1> Username or Password not correct </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }

        out.println("</BODY>");
        out.println("</HTML>");
    }
}