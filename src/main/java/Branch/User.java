package Branch;

import java.util.regex.Pattern;

public abstract class User extends Entity {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private double balance;

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(id, firstName + " " + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setPassword(password);
        this.setBalance(balance);
    }

    public void setFirstName(String fstName) {
        this.firstName = fstName;
    }

    public void setLastName(String lstName) {
        this.lastName = lstName;
    }

    public static boolean isValid(String email) {
        String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    public void setEmail(String userEmail) {
        if (isValid(userEmail) == true) {
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
        if (pass.length() >= 6) {
            this.password = pass;
        } else {
            System.out.println("[Error]: Password must have more than 6 digits");
        }
    }

    public void setBalance(double amount) {
        if (amount < 0) {
            System.out.println("[Error]: Amount must NOT be negative");
        } else {
            this.balance += amount;
        }
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

    public double getBalance() { return balance; }
}
