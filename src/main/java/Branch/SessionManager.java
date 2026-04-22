package Branch;

import java.util.prefs.Preferences;

public class SessionManager {
    private static User currentUser;
    private static final Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
    private static final String USER_EMAIL_KEY = "remembered_user_email";

    public static String getSavedEmail() {
        return prefs.get(USER_EMAIL_KEY, null);
        // Trả về null nếu không có
    }

    public static void loginCurrentUser(User user) {
        if (user == null) {
            System.err.println("Login failed: User object is null.");
            return;
        }

        currentUser = user;
        prefs.put(USER_EMAIL_KEY, user.getEmail());
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logoutCurrentUser() {
        currentUser = null;
        prefs.remove(USER_EMAIL_KEY);
    }
}
