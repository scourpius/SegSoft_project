package src.AccessController;

public class Capability {
    private String token;
    private Role role;

    public Capability(String encryptedToken, Role role) {
        this.token = encryptedToken;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Role getRole() {
        return role;
    }
}
