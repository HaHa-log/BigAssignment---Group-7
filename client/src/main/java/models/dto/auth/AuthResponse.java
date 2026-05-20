package models.dto.auth;

public class AuthResponse {
    private int userId;
    private String fullName;
    private String email;
    private String role;
    private double balance;

    public AuthResponse() {}

    public AuthResponse(int userId, String fullName, String email, String role, double balance) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public double getBalance() {
        return balance;
    }
}