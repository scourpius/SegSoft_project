package src;

import javax.servlet.*;
import javax.servlet.http.*;
import src.Exceptions.*;
import java.io.*;

public class ChangePasswordAuth extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String rName = request.getParameter("username");
        String rPwd = request.getParameter("password");
        String rPwd2 = request.getParameter("password2");


        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("</HEAD>");
        out.println("<BODY>");

        Authenticator auth = new AuthenticatorImpl();

        try {
            auth.change_pwd(rName, rPwd, rPwd2);
            response.sendRedirect("/myApp/main");
            
        } catch (PasswordsDontMatchException e) {
            out.println("<H1> Passwords don't match </H1>");
        } catch (UndefinedAccountException e){
            out.println("<H1> Username doesn't exist </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }

        out.println("</BODY>");
        out.println("</HTML>");
    }
}