package src;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


public class AuthenticatorImpl implements Authenticator {
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[] { 'F', 'C', 'T', '/', 'U', 'N', 'L', 'r',
                                                        'o', 'c', 'k','s', '!', '!', 'd', 'i' };
    private Key key = new SecretKeySpec(keyValue, ALGO);

    public AuthenticatorImpl() {
        
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

    @Override
    public String encrypt(String data){
        try{
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encVal);
        }catch(Exception e){
            return null;
        }
    }
}
