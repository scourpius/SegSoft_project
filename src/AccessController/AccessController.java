package src.AccessController;

import src.Account;
import src.Authenticator;
import src.AuthenticatorImpl;
import src.PageObject;
import src.SN;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;
import java.util.UUID;


public class AccessController {
    private static String SECRET_KEY = "SegSoftRules";

    List<Role> roleList = new ArrayList<>();

    public Role newRole (String roleId){
        Role role = new Role(roleId);
        roleList.add(role);
        return role;
    }

    public void setRole (Account user, Role role){
        role.addUser(user);
    }

    public List<Role> getRoles (Account user){
        List<Role> roles = new ArrayList<>();
        for (Role r : roleList){
            if (r.hasUser(user))
                roles.add(r);
        }
        return roles;
    }

    public void grantPermission (Role role, Resource res, Operation op){
        // TODO
    }

    public void revokePermission (Role role, Resource res, Operation op){
        // TODO
    }

    public Capability makeKey (Role role){
        //TODO need to save the token somewhere for comparison later

        String token = generateUniqueToken();
        String encryptedToken = encryptToken(token);

        Capability cap = new Capability(encryptedToken);

        return cap;
    }

    public boolean checkPermission (Capability cap, Resource res, Operation op){
        //TODO

        return false;
    }

    private String generateUniqueToken(){
        return UUID.randomUUID().toString();
    }

    private String encryptToken (String token){
        try {
            DESKeySpec keySpec = new DESKeySpec(SECRET_KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(token.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
