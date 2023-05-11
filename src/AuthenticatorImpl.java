package src;

import src.Exceptions.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AuthenticatorImpl extends HttpServlet implements Authenticator {

    private final String databaseURL = "./accounts.db";
    private static final String tableName = "Accounts";

    private static List<Account> accountList;

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[] { 'F', 'C', 'T', '/', 'U', 'N', 'L', 'r',
                    'o', 'c', 'k','s', '!', '!', 'd', 'i' };

    Key key = new SecretKeySpec(keyValue, ALGO);


    public AuthenticatorImpl() {
        try (Connection conn = connect()){
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                    "name text PRIMARY KEY,\n" +
                    "password text NOT NULL\n" +
                    ");");

            accountList = new ArrayList<>();

            ResultSet rs;
            String sql = "SELECT * FROM ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tableName);
            rs = pstmt.executeQuery();
            while (rs.next()){
                String name = rs.getString("name");
                String pwd = rs.getString("password");
                accountList.add(new Account(name, pwd));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create_account(String name, String pwd1, String pwd2) throws Exception {
        if (!pwd1.equals(pwd2)) {
            System.out.println("Passwords provided are not equal");
            return;
        }

        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                System.out.println("Account already exists");
                return;
            }
        }

        String encPass = encrypt(pwd1);
        try (Connection conn = connect()){
            String sql = "INSERT INTO ? VALUES(?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tableName);
            pstmt.setString(2, name);
            pstmt.setString(3, encPass);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        accountList.add(new Account(name, pwd1));
    }

    @Override
    public void delete_account(String name) {
        Account x = null;
        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                x = a;
                break;
            }
        }

        if (x == null){
            System.out.println("Account doesn't exist");
            return;
        }

        try (Connection conn = connect()){
            String sql = "DELETE FROM ? WHERE name=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tableName);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        accountList.remove(x);
    }

    @Override
    public Account get_account(String name) {
        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                Account newA = new Account(name, a.getPassword());

                if (a.isLogged())
                    newA.login();
                else
                    newA.logout();

                if (a.isLocked())
                    newA.lock();
                else
                    newA.unlock();

                return newA;
            }
        }
        System.out.println("Account doesn't exist");
        return null;
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) throws Exception {
        if (!pwd1.equals(pwd2)) {
            System.out.println("Passwords provided are not equal");
            return;
        }

        Account x = null;
        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                x = a;
                break;
            }
        }
        if (x == null) {
            System.out.println("User doesn't exist");
            return;
        }

        String encPass = encrypt(pwd1);
        try (Connection conn = connect()){
            String sql = "UPDATE ? SET password = ? WHERE name=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tableName);
            pstmt.setString(2, encPass);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        x.change_pwd(encPass);
    }

    @Override
    public Account authenticate_user(String name, String pwd) throws Exception, LockedAccountException, AuthenticationErrorException, UndefinedAccountException {
        for (Account a : accountList){
            if (a.getUsername().equals(name)){
                if (a.isLocked())
                    throw new LockedAccountException();
                String encPass = encrypt(pwd);
                if (!encPass.equals(a.getPassword()))
                    throw new AuthenticationErrorException();
                a.login();
                return a;
            }
        }
        throw new UndefinedAccountException();
    }

    @Override
    public void logout(Account acc) throws AlreadyAuthenticatedException {
        if (!acc.isLogged())
            throw new AlreadyAuthenticatedException();
        acc.logout();
    }

    //TODO: Finish this method
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

    public String encrypt(String Data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return java.util.Base64.
        getEncoder().encodeToString(encVal);
    }

    public String decrypt(String encrypted) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = java.util.Base64.
                getDecoder().decode(encrypted);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }
}
