package src.AccessController;

import java.util.List;

public class Capability {
    private String token;

    public Capability(String encryptedToken) {
        this.token = encryptedToken;
    }

    public String getToken() {
        return token;
    }
}
