package com.group7.dto.user;

public class UserResponse {
    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private boolean blocked;
    private double balance;
    private String avatarPath;

    public UserResponse(
            int id,
            String fullName,
            String email,
            String phoneNumber,
            String role,
            boolean blocked,
            double balance,
            String avatarPath) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.blocked = blocked;
        this.balance = balance;
        this.avatarPath = avatarPath;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public boolean isBlocked() { return blocked; }
    public double getBalance() { return balance; }
    public String getAvatarPath() { return avatarPath; }
}
