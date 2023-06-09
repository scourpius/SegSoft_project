package src;

import src.AccessController.Role;

import java.util.List;

public class Account implements Cloneable{

    private String username, password;
    private boolean logged_in, locked;
    private Role role;
    private List<PageObject> pages, followed;

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

    public void setPages(List<PageObject> pages){
        this.pages = pages;
    }

    public void addPage(PageObject page){
        this.pages.add(page);
    }

    public List<PageObject> getPages(){
        return this.pages;
    }

    public void setFollow(List<PageObject> follows){
        this.followed = follows;
    }

    public List<PageObject> getFollow(){
        return this.followed;
    }

    public void remove(PageObject page){
        this.pages.remove(page);
        this.followed.remove(page);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}