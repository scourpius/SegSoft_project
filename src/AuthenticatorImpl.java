package src;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;


public class AuthenticatorImpl implements Authenticator {

    private final String databaseURL;

    /**
     * TODO: Finish this
     */
    public AuthenticatorImpl(String databaseURL) {
        this.databaseURL = databaseURL;
        Connection conn = connect();
        try {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS Accounts(\n)" +
                    "name text PRIMARY KEY,\n" +
                    "pwd ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create_account(String name, String pwd1, String pwd2) {

    }

    @Override
    public void delete_account(String name) {

    }

    @Override
    public Account get_account(String name) {
        return null;
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) {

    }

    @Override
    public Account authenticate_user(String name, String pwd) {
        return null;
    }

    @Override
    public void logout(Account acc) {

    }

    @Override
    public Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    private Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(databaseURL);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; //should never happen
    }
}
