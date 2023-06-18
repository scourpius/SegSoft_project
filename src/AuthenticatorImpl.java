package src;

import src.AccessController.*;
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

    private SN network;

    private static List<Account> accountList;

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[] { 'F', 'C', 'T', '/', 'U', 'N', 'L', 'r',
                    'o', 'c', 'k','s', '!', '!', 'd', 'i' };

    Key key = new SecretKeySpec(keyValue, ALGO);

    private AccessController accessController;

    private static AuthenticatorImpl instance;

    private AuthenticatorImpl() {     
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            
        }  

        try (Connection conn = connect()){
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                    "username text PRIMARY KEY,\n" +
                    "role text NOT NULL,\n" +
                    "password text NOT NULL\n" +
                    ");");

            accountList = new ArrayList<>();
            accessController = new AccessController();

            ResultSet rs;
            String sql = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                String name = rs.getString("username");
                String pwd = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                accountList.add(new Account(name, pwd, role));
            }

            sql = "INSERT OR IGNORE INTO " + tableName + " VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "root");
            pstmt.setString(2, encrypt("1234"));
            pstmt.executeUpdate();

            if (get_account("root") == null)
                accountList.add(new Account("root", encrypt("1234"), Role.Admin));

            network = new SN();
            network.DBBuild();
            for (PageObject page : network.getAllPages()){
                for (Account acc : accountList)
                    if (acc.getUsername().equals(page.getUserID())) {
                        acc.addPage(page);
                        for (PostObject post : network.getAllPosts())
                            if (post.getPageId() == page.getPageId())
                                page.addPost(post);
                        break;
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized AuthenticatorImpl getInstance() {
        if (instance == null) {
            instance = new AuthenticatorImpl();
        }

        return instance;
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

        accountList.add(new Account(name, encPass, Role.User));
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
    public Account get_account(String name) throws CloneNotSupportedException {
        for (Account a : accountList) {
            if (a.getUsername().equals(name))
                return (Account) a.clone();
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
            pstmt.setString(1, encPass);
            pstmt.setString(2, name);

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
        for (Account a : accountList)
            if (a.getUsername().equals(acc.getUsername())) {
                a.logout();
                break;
            }
        acc.logout();
    }

    @Override
    public void create_page(String username, String pageEmail, String pagename, String pagePic) throws SQLException, UndefinedAccountException {
        Account acc = null;
        for (Account a : accountList)
            if (a.getUsername().equals(username)) {
                acc = a;
                break;
            }
        if (acc == null)
            throw new UndefinedAccountException();
        PageObject page = network.newPage(username, pageEmail, pagename, pagePic);
        acc.addPage(page);
    }

    @Override
    public void delete_page(int pageID) throws PageDoesNotExistException, SQLException {
        PageObject page = network.getPage(pageID);
        if (page.getUserID() == null)
            throw new PageDoesNotExistException();
        network.deletePage(page);
        for (Account acc : accountList)
            if (acc.getUsername().equals(page.getUserID()))
                acc.removePage(page);
    }

    @Override
    public void create_post(int pageID, String postTime, String postText) throws PageDoesNotExistException, SQLException {
        PageObject page = network.getPage(pageID);
        if (page.getUserID() == null)
            throw new PageDoesNotExistException();
        PostObject post = network.newPost(pageID, postTime, postText);
        page.addPost(post);
    }

    @Override
    public void delete_post(int postID) throws SQLException, PostDoesNotExistException {
        PostObject post = network.getPost(postID);
        if (post.getPostDate() == null)
            throw new PostDoesNotExistException();
        network.deletePost(post);
        PageObject page = network.getPage(post.getPageId());
        for (Account acc : accountList)
            if (acc.getUsername().equals(page.getUserID()))
                for (PageObject page2 : acc.getPages())
                    if (page2.getPageId() == page.getPageId()) {
                        page2.removePost(post);
                        break;
                    }
    }

    @Override
    public Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationErrorException, PermissionDeniedException, CloneNotSupportedException {
        HttpSession session = request.getSession(false);

        if (session == null)
            throw new AuthenticationErrorException();
        
        String username = (String) session.getAttribute("USER");
        String password = (String) session.getAttribute("PWD");

        Account a = get_account(username);

        if (a != null && a.isLogged() && a.getPassword().equals(password) && !a.isLocked()){
            String operation = (String) session.getAttribute("OP");
            Capability cap = (Capability) session.getAttribute("CAP");

            switch (operation){
                case "change_pwd": if (!((String)session.getAttribute("name")).equals(username)) throw new PermissionDeniedException();
                case "create_account":
                case "delete_account": if (!(username.equals("root"))) throw new PermissionDeniedException(); break;

                //TODO decide between these 2 options

                //option 1: (probably more secure, bigger code)
                case "create_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.create)) throw new PermissionDeniedException(); break;
                case "delete_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.delete)) throw new PermissionDeniedException(); break;
                case "list_pages": if (!accessController.checkPermission(cap, Resource.Page, Operation.access)) throw new PermissionDeniedException(); break;
                case "follow_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.submit_follow)) throw new PermissionDeniedException(); break;
                case "authorize_follow_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.authorize_follow)) throw new PermissionDeniedException(); break;
                case "like_post":
                    if (!accessController.checkPermission(cap, Resource.Post, Operation.like)) {
                        String postID = (String) session.getAttribute("POSTID");
                        for (PageObject page : a.getFollowedPages())
                            for (PostObject post : page.getPosts())
                                if (post.getPostId().equals(postID))
                        throw new PermissionDeniedException();
                    }
                    break;
                default:
            }

            return a;
        }

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

    @Override
    public Capability getCapability(Role role) {
        return accessController.makeKey(role);
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
