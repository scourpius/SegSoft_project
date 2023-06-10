package src.AccessController;

import src.Account;
import src.Authenticator;
import src.AuthenticatorImpl;

import java.util.ArrayList;
import java.util.List;

public class AccessController {
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
        // TODO
        return null;
    }

    public void checkPermission (Capability cap, Resource res, Operation op){
        // TODO
        return;
    }
}
