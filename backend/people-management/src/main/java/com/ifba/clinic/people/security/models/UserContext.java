package com.ifba.clinic.people.security.models;

import com.ifba.clinic.people.models.response.UserRole;
import java.util.List;

public class UserContext {

  private static final ThreadLocal<Boolean> isSystemCall = new ThreadLocal<>();

  private static final ThreadLocal<String> userId = new ThreadLocal<>();
  private static final ThreadLocal<String> userEmail = new ThreadLocal<>();
  private static final ThreadLocal<List<UserRole>> userRoles = new ThreadLocal<>();
  private static final ThreadLocal<List<String>> userTraits = new ThreadLocal<>();

  public static void setContext(String id, String email, List<UserRole> roles, List<String> traits) {
    userId.set(id);
    userEmail.set(email);
    userRoles.set(roles);
    userTraits.set(traits);
  }

  public static void setSystemCallContext() {
    isSystemCall.set(true);
  }

  public static String getUserId() {
    return userId.get();
  }

  public static String getUserEmail() {
    return userEmail.get();
  }

  public static List<UserRole> getUserRoles() {
    return userRoles.get();
  }

  public static List<String> getUserTraits() {
    return userTraits.get();
  }

  public static Boolean isSystemCall() {
    if (isSystemCall.get() == null) {
      return false;
    }

    return isSystemCall.get();
  }

  public static void clear() {
    userId.remove();
    userEmail.remove();
    userRoles.remove();
    userTraits.remove();

    isSystemCall.remove();
  }

}
