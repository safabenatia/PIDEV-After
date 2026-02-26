package utils;

import models.Users;

public class Session {
    private static Users currentUser;
    private static String jwtToken;

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    public static void clear() {
        currentUser = null;
        jwtToken = null;
    }
}