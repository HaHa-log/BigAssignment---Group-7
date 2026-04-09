import java.util.HashMap;

public class TempDatabase {
    private static HashMap<String, User> userDatabase = new HashMap<>();

    public static void saveUser(User user) {
        userDatabase.put(user.getEmail(), user);
        System.out.println("[System]: User " + user.getFirstName() + " saved to temporary storage.");
    }

    public static User getUserByEmail(String email) {
        return userDatabase.get(email);
    }

    //Database accesses account via email, auction accesses account via user id
}
