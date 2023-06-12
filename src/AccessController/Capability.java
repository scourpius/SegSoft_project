package src.AccessController;

import java.util.List;

public class Capability {
    private String token;
    List<Permission> permissions;
    
    public Capability(String encryptedToken, List<Permission> permissions) {
        this.token = encryptedToken;
        this.permissions = permissions;
    }

    public String getToken() {
        return token;
    }

    public List<Permission> getPermissions(){
        return this.permissions;
    }
}
