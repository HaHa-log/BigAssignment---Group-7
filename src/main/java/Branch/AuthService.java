package Branch;

public class AuthService {
    public static Member registerNewUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (email.isEmpty()) {
            //return "[Failure]: An email is required";
            return null;
        }

        if (TempDatabase.getUserByEmail(email) != null) {
            //return "[Failure]: An account with this email already exists";
            return null;
        }

        int id = (int) (Math.random() * 1000);
        Member member = new Member(id, firstName, lastName, email, phoneNumber, password);

        TempDatabase.saveUser(member);

        //return "You've created a new account!";
        return member;
    }

    public static User login(String email, String password) {
        for (User user : TempDatabase.getAllUsers()) {
            boolean isMatch = user.getEmail().equals(email);

            if (isMatch && user.getPassword().equals(password)) {
                return user;
                //return "[System]: Login successful. Welcome back, " + user.getFirstName();
            }
        }

        return null;
        //return "[Failure]: Invalid email/username or password.";
    }
}
