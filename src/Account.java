package src;

import src.AccessController.Role;

public class Account{

    private String username, password;
    private boolean logged_in, locked;
    private Role role;

    public Account(String username, String password, Role role){
        this.username = username;
        this.password = password;
        this.logged_in = false;
        this.locked = false;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogged() {
        return this.logged_in;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void login() {
        this.logged_in = true;
    }

    public void logout() {
        this.logged_in = false;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public void change_pwd(String newPwd) {
        this.password = newPwd;
    }

    public Role getRole(){
        return this.role;
    }

    public void changeRole(Role role){
        this.role = role;
    }
}