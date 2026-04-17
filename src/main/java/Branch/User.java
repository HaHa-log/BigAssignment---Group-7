package Branch;

import java.util.regex.Pattern;
import java.time.LocalDateTime;

public abstract class User extends Entity {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private double balance;
    private boolean isAdmin = false;
    private boolean isBlocked = false;
    private LocalDateTime blockedUntil = null;

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName + " " + lastName);
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

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    public void setEmail(String userEmail) {
        if (isValidEmail(userEmail) == true) {
            this.email = userEmail;
        } else {
            throw new IllegalArgumentException("[Error]: Invalid email format");
        }
    }

    public static boolean isValidPhoneNumber(String phone) {
        String regex = "^0\\d{9}$";
        return phone != null && phone.matches(regex);
    }

    public void setPhoneNumber(String contactNumber) {
        if (isValidPhoneNumber(contactNumber) == true) {
            this.phoneNumber = contactNumber;
        } else {
            throw new IllegalArgumentException("[Error]: Invalid phone number format");
        }
    }

    public void setPassword(String pass) {
        if (pass.length() >= 6) {
            this.password = pass;
        } else {
            throw new IllegalArgumentException("[Error]: Password must have more than 6 digits");
        }
    }

    public void setBalance(double amount) {
        if (amount < 0) {
            System.out.println("[Error]: Amount must NOT be negative");
        } else {
            this.balance += amount;
        }
    }

    public void setBlocked(LocalDateTime until) {
        this.isBlocked = true;
        this.blockedUntil = until;
    }

    public int getId() {
        return Database.getUserId(this);
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

    public boolean isAdmin() { return isAdmin; }

    public boolean isBlocked() {
        if (isBlocked && blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil)) {
            isBlocked = false;
            blockedUntil = null;
        }
        return isBlocked;
    }

    public void isUnblocked() {
        this.isBlocked = false;
        this.blockedUntil = null;
    }
}
