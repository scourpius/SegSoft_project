import src.Authenticator;
import src.AuthenticatorImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;

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

            Authenticator auth = new AuthenticatorImpl("accounts.db");

        try {
            auth.create_account(rName, rPwd, rPwd2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //TODO in the future this will check for encrypted passwords + create session

            out.println("</BODY>");
            out.println("</HTML>");
    }
}