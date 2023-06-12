package src.AccessController;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;
import java.util.LinkedList;
import java.util.UUID;


public class AccessController {
    private static String SECRET_KEY = "SegSoftRules";

    private String currToken;

    List<Permission> userPermissions = new LinkedList<>();
    List<Permission> adminPermissions = new LinkedList<>();

    public AccessController() {
        for (Operation op : Operation.values()) {
            Permission temp;
            switch (op){
                case access:
                    temp = new Permission(Resource.Post, op);
                    userPermissions.add(temp);
                    adminPermissions.add(temp);
                    temp = new Permission(Resource.Page, op);
                    userPermissions.add(temp);
                    adminPermissions.add(temp);
                    break;

                case delete:
                case create:
                    temp = new Permission(Resource.Post, op);
                    userPermissions.add(temp);
                    adminPermissions.add(temp);
                    temp = new Permission(Resource.Page, op);
                    adminPermissions.add(temp);
                    break;

                case submit_follow:
                case authorize_follow:
                    temp = new Permission(Resource.Page, op);
                    userPermissions.add(temp);
                    adminPermissions.add(temp);
                    break;

                case like:
                    temp = new Permission(Resource.Post, op);
                    userPermissions.add(temp);
                    adminPermissions.add(temp);
                    break;

                default:
                break;
            }
        }
    }

    public void grantPermission (Role role, Resource res, Operation op){
        if (role == Role.Admin)
            adminPermissions.add(new Permission(res, op));
        else
            userPermissions.add(new Permission(res, op));
    }

    public void revokePermission (Role role, Resource res, Operation op){
        if (role == Role.Admin)
            adminPermissions.remove(new Permission(res, op));
        else
            userPermissions.remove(new Permission(res, op));
    }

    public Capability makeKey (Role role){
        String token = generateUniqueToken();
        String encryptedToken = encryptToken(token);
        currToken = encryptedToken;

        List<Permission> permissions = role == Role.Admin ? adminPermissions : userPermissions;

        Capability cap = new Capability(encryptedToken, permissions);

        return cap;
    }

    public boolean checkPermission (Capability cap, Resource res, Operation op){
        if (cap.getToken().equals(currToken)){
            List<Permission> permissions = cap.getPermissions();
            Permission aux = new Permission(res, op);

            return permissions.contains(aux);
        }else{
            //TODO create new token
        }

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
