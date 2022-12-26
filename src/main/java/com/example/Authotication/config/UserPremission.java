package com.example.Authotication.config;

public enum UserPremission {
    ;
    private final String permission;

    UserPremission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

}
