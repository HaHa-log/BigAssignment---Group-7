package Branch;

import java.util.regex.Pattern;

public abstract class User extends Entity {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName + " " + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public void setFirstName(String fstName) {
        this.firstName = fstName;
    }

    public void setLastName(String lstName) {
        this.lastName = lstName;
    }

    public class EmailValidator {
        private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        public static boolean isValid(String email) {
            return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
        }
    }

    public void setEmail(String userEmail) {
        if (EmailValidator.isValid(userEmail)) {
            this.email = userEmail;
        } else {
            System.out.println("[Error]: Invalid email format");
        }
    }

    public boolean isValidPhoneNumber(String phone) {
        String regex = "^0\\d{9}$";
        return phone != null && phone.matches(regex);
    }

    public void setPhoneNumber(String contactNumber) {
        if (isValidPhoneNumber(contactNumber) == true) {
            this.phoneNumber = contactNumber;
        } else {
            System.out.println("[Error]: Invalid phone number format");
        }
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

}
