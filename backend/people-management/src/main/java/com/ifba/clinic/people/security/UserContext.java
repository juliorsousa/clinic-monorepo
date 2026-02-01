package com.ifba.clinic.people.security;

public class UserContext {
    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> userEmail = new ThreadLocal<>();

    public static void setContext(String id, String email) {
        userId.set(id);
        userEmail.set(email);
    }

    public static String getUserId() { return userId.get(); }
    public static void clear() { userId.remove(); userEmail.remove(); }
}
