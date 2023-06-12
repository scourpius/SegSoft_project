package src.AccessController;

public class Permission {
    private Resource resource;
    private Operation operation;

    public Permission(Resource res, Operation op){
        this.resource = res;
        this.operation = op;
    }

    public Resource getResource(){
        return this.resource;
    }

    public Operation getOperation(){
        return this.operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        Permission perm = (Permission) obj;

        return (this.resource == perm.getResource() && this.operation == perm.getOperation());
    }
}
