package Branch;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    public static Member registerNewUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        List<String> errors = new ArrayList<>();

        //Testing: Ban đầu chỉ in lỗi tuần tự chứ không in ra hết lỗi -> đã sửa bên dưới

        if (firstName.isEmpty() || lastName.isEmpty()) {
            errors.add("[Error: Full Name is required");
        }
        if (email.isEmpty()) {
            errors.add("[Error]: Email is required");
        } else if (!User.isValidEmail(email)) {
            errors.add("[Error]: Invalid email format");
        } else if (TempDatabase.getUserByEmail(email) != null) {
            errors.add("[Error]: An account with this email already exists");
        }
        if (!User.isValidPhoneNumber(phoneNumber)) {
            errors.add("[Error]: Invalid phone number format");
        }
        if (password.length() < 6) {
            errors.add("[Error]: Password must have at least 6 characters");
        }

        boolean allEmpty = firstName.isEmpty() && lastName.isEmpty() &&
                email.isEmpty() && phoneNumber.isEmpty() && password.isEmpty();
        if (allEmpty) {
            System.out.println("[Error]: Missing information");
            return null;
        }
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            return null;
        }

        try {
            double balance = 0.0;
            Member member = new Member(firstName, lastName, email, phoneNumber, password, balance);
            TempDatabase.saveUser(member);
            //return "[System]: You've created a new account!";
            return member;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static User login(String email, String password) {
        User user = TempDatabase.getUserByEmail(email);

        if (user.getPassword().equals(password)) {
            return user;
                //return "[System]: Login successful. Welcome back, " + user.getFirstName();
        }


        return null;
        //return "[Failure]: Invalid email/username or password.";
    }
}
