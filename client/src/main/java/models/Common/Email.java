package models.Common;

import java.util.regex.Pattern;

public class Email {
    private final String email;

    public Email(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("[Error]: Invalid email format");
        }
        this.email = email;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    @Override
    public String toString() {
        return email;
    }
}