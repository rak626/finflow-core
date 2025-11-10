package com.rakesh.finflow.identity.utils;

public class UserNameValidator {

    public static boolean userNameNotEmpty(String username) {
        return username != null && !username.trim().isEmpty();
    }
}
