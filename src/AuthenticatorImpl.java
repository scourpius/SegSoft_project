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


public class AuthenticatorImpl extends HttpServlet {

    private final String databaseURL = "jdbc:sqlite:accounts.db";
    private static final String tableName = "Accounts";

    private SN network;

    private static List<Account> accountList;

    private Account account;

    private List<PageObject> pageList;

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
            Statement stmt = conn.createStatement();
            
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                    "username text PRIMARY KEY,\n" +
                    "role text NOT NULL,\n" +
                    "password text NOT NULL\n" +
                    ");");

            accountList = new ArrayList<>();
            accessController = new AccessController();

            ResultSet rs;
            String sql = "SELECT * FROM " + tableName;
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                String name = rs.getString("username");
                String pwd = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                accountList.add(new Account(name, pwd, role));
            }

            sql = "INSERT OR IGNORE INTO " + tableName + " VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "root");
            pstmt.setString(2, Role.Admin.name());
            pstmt.setString(3, encrypt("1234"));
            pstmt.executeUpdate();

            if (get_account("root") == null)
                accountList.add(new Account("root", encrypt("1234"), Role.Admin));

            network = new SN();
            network.DBBuild();

            pageList = network.getAllPages();

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

    public Account get_account(String name) throws CloneNotSupportedException {
        for (Account a : accountList) {
            if (a.getUsername().equals(name))
                return (Account) a.clone();
        }
        return null;
    }

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

    public Account authenticate_user(String name, String pwd) throws Exception, LockedAccountException, AuthenticationErrorException, UndefinedAccountException {
        for (Account a : accountList){
            if (a.getUsername().equals(name)){
                if (a.isLocked())
                    throw new LockedAccountException();
                
                String encPass = encrypt(pwd);

                if (!encPass.equals(a.getPassword()))
                    throw new AuthenticationErrorException();

                a.setPages(network.getPages(name));

                List<PageObject> followedPages = new ArrayList<>();

                for (PageObject page : a.getPages())
                    followedPages.addAll(network.getfollowed(page.getPageId()));
                
                a.setFollow(followedPages);

                a.login();

                account = a;
                
                return a;
            }
        }

        throw new UndefinedAccountException();
    }

    public void logout(Account acc) throws AlreadyAuthenticatedException {
        if (!acc.isLogged())
            throw new AlreadyAuthenticatedException();
        for (Account a : accountList)
            if (a.getUsername().equals(acc.getUsername())) {
                a.logout();
                
                break;
            }
        acc.logout();
        account = null;
    }

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

        if (account.getUsername().equals(username)){
            account.addPage(page);
            acc.addPage(page);
        }
    }

    public void delete_page(int pageID) throws PageDoesNotExistException, SQLException {
        PageObject page = network.getPage(pageID);

        if (page.getUserID() == null)
            throw new PageDoesNotExistException();
        network.deletePage(page);

        account.remove(page);
    }

    public void create_post(int pageID, String postTime, String postText) throws PageDoesNotExistException, SQLException, AuthenticationErrorException {
        PageObject page = network.getPage(pageID);
        if (page.getUserID() == null)
            throw new PageDoesNotExistException();
        if (!account.getPages().contains(page))
            throw new AuthenticationErrorException();
        network.newPost(pageID, postTime, postText);
    }

    public void delete_post(int postID) throws SQLException, PostDoesNotExistException, AuthenticationErrorException {
        PostObject post = network.getPost(postID);
        if (post.getPostDate() == null)
            throw new PostDoesNotExistException();
        PageObject page = network.getPage(post.getPageId());
        if (!account.getPages().contains(page))
            throw new AuthenticationErrorException();
        network.deletePost(post);
    }

    public List<PostObject> access_posts (int pageID) throws SQLException, PermissionDeniedException {
        PageObject page = network.getPage(pageID);
        if (!(account.getFollow().contains(page) || account.getPages().contains(page)))
            throw new PermissionDeniedException();
        return network.getPagePosts(pageID);
    }

    public void like_post (int postID) throws SQLException, PermissionDeniedException {
        PostObject post = network.getPost(postID);
        PageObject page = network.getPage(post.getPageId());
        if (!(account.getFollow().contains(page) || account.getPages().contains(page)))
            throw new PermissionDeniedException();

        page = getMainPage();
        if (network.getLikes(postID).contains(page))
            network.unlike(postID, page.getPageId());
        else
            network.like(postID, page.getPageId());
    }

    public void follow_page (int pageID) throws SQLException, PermissionDeniedException {
        PageObject page = network.getPage(pageID);
        if (!(account.getFollow().contains(page) || account.getPages().contains(page)))
            throw new PermissionDeniedException();

        page = getMainPage();
        network.follows(page.getPageId(), pageID, FState.PENDING);
    }

    public void authorize_follow (int pageID, int followID, boolean authorized) throws SQLException, PermissionDeniedException {
        if (!account.getPages().contains(network.getPage(pageID)))
            throw new PermissionDeniedException();
        if (authorized)
            network.updatefollowsstatus(followID, pageID, FState.OK);
        else
            network.updatefollowsstatus(followID, pageID, FState.NONE);
    }

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
                case "logout": return a;
                case "create_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.create)) throw new PermissionDeniedException(); break;
                case "delete_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.delete)) throw new PermissionDeniedException(); break;
                case "list_pages": if (!accessController.checkPermission(cap, Resource.Page, Operation.access)) throw new PermissionDeniedException(); break;
                case "follow_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.submit_follow)) throw new PermissionDeniedException(); break;
                case "authorize_follow": if (!accessController.checkPermission(cap, Resource.Page, Operation.authorize_follow)) throw new PermissionDeniedException(); break;
                case "like_post": if (!accessController.checkPermission(cap, Resource.Page, Operation.like)) throw new PermissionDeniedException(); break;
                case "create_post": if (!accessController.checkPermission(cap, Resource.Post, Operation.create)) throw new PermissionDeniedException(); break;
                case "delete_post": if (!accessController.checkPermission(cap, Resource.Post, Operation.delete)) throw new PermissionDeniedException(); break;
                case "access_post": if (!accessController.checkPermission(cap, Resource.Post, Operation.access)) throw new PermissionDeniedException(); break;
                case "show_page": if (!accessController.checkPermission(cap, Resource.Page, Operation.access)) throw new PermissionDeniedException(); break;
                default: throw new PermissionDeniedException();
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

    public List<Account> userList() {
        return accountList;
    }

    public List<PageObject> pageList(){
        return pageList;
    }

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

    /**
     * @return User's main page to be used during follow and like operations
     * @throws PermissionDeniedException when user does not have a page associated
     */
    private PageObject getMainPage() throws PermissionDeniedException {
        if (account.getPages().size() < 1)
            throw new PermissionDeniedException();
        return account.getPages().get(0);
    }
}
