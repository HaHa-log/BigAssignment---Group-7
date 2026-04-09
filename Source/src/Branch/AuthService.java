public class AuthService {
    public String registerNewUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (email.isEmpty() || password.length() < 6) {
            return "Failure: email and a password length >= 6 are required";
        }

        int id = (int) (Math.random() * 1000);
        Member member = new Member(id, firstName, lastName, email, phoneNumber, password);

        TempDatabase.saveUser(member);

        return "You've created a new account!";
    }
}
