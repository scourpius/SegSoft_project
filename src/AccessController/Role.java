package src.AccessController;

import src.Account;

import java.util.ArrayList;
import java.util.List;

public class Role {
    String id;
    List<Account> userList = new ArrayList<>();
    public Role(String roleId) {
        id = roleId;
    }

    public void addUser(Account user) {
        userList.add(user);
    }

    public boolean hasUser(Account user) {
        return userList.contains(user);
    }
}
