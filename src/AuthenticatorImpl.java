package src;

import src.Exceptions.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AuthenticatorImpl extends HttpServlet implements Authenticator {

    private final String databaseURL = "jdbc:sqlite:accounts.db";
    private static final String tableName = "Accounts";

    private static List<Account> accountList;

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[] { 'F', 'C', 'T', '/', 'U', 'N', 'L', 'r',
                    'o', 'c', 'k','s', '!', '!', 'd', 'i' };

    Key key = new SecretKeySpec(keyValue, ALGO);

    public AuthenticatorImpl() {     
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            
        }  

        try (Connection conn = connect()){
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                    "username text PRIMARY KEY,\n" +
                    "password text NOT NULL\n" +
                    ");");

            accountList = new ArrayList<>();

            ResultSet rs;
            String sql = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                String name = rs.getString("username");
                String pwd = rs.getString("password");
                accountList.add(new Account(name, pwd));
            }

            sql = "INSERT OR IGNORE INTO " + tableName + " VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "root");
            pstmt.setString(2, encrypt("1234"));
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create_account(String name, String pwd1, String pwd2) throws AccountExistsException, PasswordsDontMatchException, Exception {
        if (!pwd1.equals(pwd2)) {
            throw new PasswordsDontMatchException();
        }

        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                System.out.println("Account already exists");
                throw new AccountExistsException();
            }
        }
        
        String encPass = encrypt(pwd1);

        try (Connection conn = connect()){
            String sql = "INSERT INTO " + tableName + " VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, encPass);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception();
        }

        accountList.add(new Account(name, pwd1));
    }

    @Override
    public void delete_account(String name) throws UndefinedAccountException, Exception{
        Account x = null;
        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                x = a;
                break;
            }
        }

        if (x == null){
            throw new UndefinedAccountException();
        }

        try (Connection conn = connect()){
            String sql = "DELETE FROM " + tableName + " WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception();
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

        return null;
    }

    @Override
    public void change_pwd(String name, String pwd1, String pwd2) throws UndefinedAccountException, PasswordsDontMatchException, Exception {
        if (!pwd1.equals(pwd2)) {
            throw new PasswordsDontMatchException();
        }

        Account x = null;
        for (Account a : accountList) {
            if (a.getUsername().equals(name)) {
                x = a;
                break;
            }
        }

        if (x == null) {
            throw new UndefinedAccountException();
        }

        String encPass = encrypt(pwd1);
        
        try (Connection conn = connect()){
            String sql = "UPDATE " + tableName + " SET password = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pwd1);
            pstmt.setString(1, name);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception();
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

    @Override
    public Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationErrorException{
        HttpSession session = request.getSession(false);

        if (session == null)
            throw new AuthenticationErrorException();
        
        String username = (String) session.getAttribute("USER");
        String password = (String) session.getAttribute("PWD");

        Account a = get_account(username);

        System.out.println(a.isLogged());
        System.out.println(a.isLocked());
        System.out.println(password);
        System.out.println(a.getPassword());



        if (a != null && a.isLogged() && a.getPassword().equals(password) && !a.isLocked())
            return a;

        throw new AuthenticationErrorException();
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

    @Override
    public List<Account> userList() {
        return accountList;
    }

    private String encrypt(String Data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return java.util.Base64.
        getEncoder().encodeToString(encVal);
    }

    private String decrypt(String encrypted) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = java.util.Base64.
                getDecoder().decode(encrypted);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }
}
