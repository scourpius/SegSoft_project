package src;

import src.Exceptions.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator {
    /*
    created account info should be stored (serialised) in
    persistent storage (e.g., file, sql(lite) database)
    • name: the account name (must be unique)
    • pwd1, pwd2: the password (in cleartext)
    • precondition: pwd1 == pwd2
    • the account pwd must be stored in encrypted form
     */
    void create_account(String name, String pwd1, String pwd2) throws AccountExistsException, PasswordsDontMatchException, Exception;

    /*
    deletes an existing account object
    • deleted account should be deleted from the
    associated persistent storage
    • preconditions
    • the account cannot be logged in
    • the account must be locked (so no one will
    authenticate on the way)
     */
    void delete_account(String name) throws UndefinedAccountException, Exception;

    //returns a clone (readonly) of an existing account object
    Account get_account(String name);

    /*
    changes the password of the account name to pwd1
    • pwd1 and pwd2 given in cleartext
    • preconditions:
    • name must identify a created account
    • pwd1 == pwd2 (see note in comments on
    create_account)
 */
    void change_pwd(String name, String pwd1, String pwd2) throws UndefinedAccountException, PasswordsDontMatchException, Exception;

    /*
    authenticates the caller, given name and password
    • checks if name is defined as an account name
    • if not, raise exception (UndefinedAccount)
    • checks if account is locked
    • if locked, raise exception (LockedAccount)
    • compares the encryption of pwd with the stored hash
    • if comparison succeeds, sets logged_in to true and
    succeeds, returning the authenticated account
    • otherwise raise exception (AuthenticationError)
    • must not let password flow anywhere else
     */
    Account authenticate_user(String name, String pwd) throws Exception, LockedAccountException, AuthenticationErrorException, UndefinedAccountException;

    /*
    the user looses its authentication
    • logs out the account a (sets logged_in to false)
    • preconditions
    • if user is not authenticated, raises exception
    (AlreadyAuthenticated)
     */
    void logout(Account acc) throws AlreadyAuthenticatedException;

    /*
    used to verify is user making the request to webApp is
    an authenticated principal.
    • to authenticate the caller in every servlet interaction
    • caller is supposed to be already logged in
    • extracts name and (hashed) credentials from req state
    • several possible implementations
    • cookie (to be avoided this)
    • use session parameters (stateless)
    • token based ( JSON Web Tokens )
    • If this fails, raise AuthenticationError exception
    • this method is supposed to be called by all application
        operations that require authentication (e.g., if the app
        needs to know who is the authority requesting the op)
     */
    Account check_authenticated_request(HttpServletRequest request, HttpServletResponse response) throws AuthenticationErrorException;

    List<Account> userList();
}
