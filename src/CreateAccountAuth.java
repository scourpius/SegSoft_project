package src;

import javax.servlet.*;
import javax.servlet.http.*;

import src.Exceptions.AccountExistsException;
import src.Exceptions.PasswordsDontMatchException;

import java.io.*;

public class CreateAccountAuth extends HttpServlet {
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
            auth.create_account(rName, rPwd, rPwd2);
            response.sendRedirect("/myApp/main");
            
        } catch (AccountExistsException e) {
            out.println("<H1> Account name already exists </H1>");
        } catch (PasswordsDontMatchException e){
            out.println("<H1> Passwords do not match </H1>");
        } catch (Exception e) {
            out.println("<H1> Error </H1>");
        }


        out.println("</BODY>");
        out.println("</HTML>");
    }
}