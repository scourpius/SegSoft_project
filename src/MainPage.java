package src;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class MainPage extends HttpServlet {
    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("</HEAD>");
            out.println("<BODY>");
            out.println("<a href='http://localhost:8080/myApp/createAccount'>");
            out.println("<button>Create Account</button>");
            out.println("</a><br><br>");
            out.println("<a href='http://localhost:8080/myApp/deleteAccount'>");
            out.println("<button>Delete Account</button>");
            out.println("</a><br><br>");
            out.println("<a href='http://localhost:8080/myApp/changePassword'>");
            out.println("<button>Change Password</button>");
            out.println("</a><br><br>");
            out.println("<a href='http://localhost:8080/myApp/listUsers'>");
            out.println("<button>List Users</button>");
            out.println("</a><br><br>");
            out.println("<a href='http://localhost:8080/myApp/logoutAuth'>");
            out.println("<button>Logout</button>");
            out.println("</a>");
            out.println("</BODY>");
            out.println("</HTML>");
    }
}