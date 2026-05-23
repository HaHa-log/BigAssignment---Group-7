package models;

import java.util.prefs.Preferences;

public class SessionManager {
    private static Member currentUser;
    private static final Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
    private static final String USER_EMAIL_KEY = "remembered_user_email";

    public static String getSavedEmail() {
        return prefs.get(USER_EMAIL_KEY, null);
    }

    public static void loginCurrentUser(Member member) {
        if (member == null) {
            System.err.println("Login failed: Member object is null.");
            return;
        }

        currentUser = member;
        prefs.put(USER_EMAIL_KEY, member.getEmail());
    }

    public static Member getCurrentUser() {
        return currentUser;
    }

    public static void logoutCurrentUser() {
        currentUser = null;
        prefs.remove(USER_EMAIL_KEY);
    }
}