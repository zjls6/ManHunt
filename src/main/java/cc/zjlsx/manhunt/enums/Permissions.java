package cc.zjlsx.manhunt.enums;

public enum Permissions {

    Admin("manhunt.admin"),
    None("");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
