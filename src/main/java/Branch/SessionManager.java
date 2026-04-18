package Branch;

public class SessionManager {
    private static User currentUser;

    public static void loginCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logoutCurrentUser() {
        currentUser = null;
    }
}
