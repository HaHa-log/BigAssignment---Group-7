package services.dto.auth;

public class AuthResponse {
    private int userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private double balance;
    private String avatarPath;

    public AuthResponse() {}

    public AuthResponse(
            int userId,
            String firstName,
            String lastName,
            String fullName,
            String email,
            String phoneNumber,
            String role,
            double balance,
            String avatarPath) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.balance = balance;
        this.avatarPath = avatarPath;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() {
        return role;
    }
    public void setRole(String role) { this.role = role; }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) { this.balance = balance; }

    public String getAvatarPath() {
        return avatarPath;
    }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}
