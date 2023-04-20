package src;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatorImpl implements Authenticator {
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
}
