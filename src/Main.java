package src;

public class Main {

    public static final String DB_URL = "jdbc:sqlite://localhost/TEST.db";


    public static void main(String[] args) {
        Authenticator auth = new AuthenticatorImpl(DB_URL);

    }
}
