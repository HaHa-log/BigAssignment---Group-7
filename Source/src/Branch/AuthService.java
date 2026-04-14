package Branch;

public class AuthService {
    public static String registerNewUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (email.isEmpty() || password.length() < 6) {
            return "[Failure]: email and a password length >= 6 are required";
        }

        if (TempDatabase.getUserByEmail(email) != null) {
            return "[Failure]: an account with this email already exists";
        }

        int id = (int) (Math.random() * 1000);
        Member member = new Member(id, firstName, lastName, email, phoneNumber, password);

        TempDatabase.saveUser(member);

        return "You've created a new account!";
    }

    public static String login(String name, String email, String password) {
        for (User user : TempDatabase.getAllUsers()) {
            boolean isMatch = user.getEmail().equals(email) ||
                    (user.getName() != null && user.getName().equals(name));

            if (isMatch && user.getPassword().equals(password)) {
                return "[System]: Login successful. Welcome back, " + user.getFirstName();
            }
        }

        return "[Failure]: Invalid email/username or password.";
    }
}
