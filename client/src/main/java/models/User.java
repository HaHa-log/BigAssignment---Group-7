package models;

import models.Common.Balance;
import models.Common.Email;
import models.Common.FullName;
import models.Common.PhoneNumber;

import java.time.LocalDateTime;
import java.util.Objects;

public class User extends Entity implements Bidder, Seller {
    private FullName fullname;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private Balance balance;
    private boolean isAdmin;
    private boolean isBlocked = false;
    private LocalDateTime blockedUntil = null;
    private String avatarPath;

    private double frozenBalance = 0;

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        this.fullname = new FullName(firstName, lastName);
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.avatarPath = avatarPath;
        this.balance = new Balance(balance);

        setPassword(password);
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {
        this(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.blockedUntil = blockedUntil;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setEmail(String email) {
        this.email = new Email(email);
    }

    public void setPhoneNumber(String number) {
        this.phoneNumber = new PhoneNumber(number);
    }

    public void setPassword(String pass) {
        if (pass == null || pass.length() < 6) {
            throw new IllegalArgumentException("[Error]: Password must have at least 6 characters.");
        }
        this.password = pass;
    }

    public boolean depositMoney(double amount) {
        return this.balance.deposit(amount);
    }

    public boolean spendFrozenMoney(double amount) {
        if (amount <= 0) return false;
        if (frozenBalance < amount) return false;

        frozenBalance -= amount;
        return true;
    }

    public double getBalance() {
        return balance.showBalance();
    }

    public double getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(double frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public void setBlocked(LocalDateTime until) {
        this.isBlocked = true;
        this.blockedUntil = until;
    }

    public boolean isBlocked() {
        if (isBlocked && blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil)) {
            this.isBlocked = false;
            this.blockedUntil = null;
        }
        return isBlocked;
    }

    public void isUnblocked() {
        this.isBlocked = false;
        this.blockedUntil = null;
    }

    public String getFullName() {
        return fullname.toString();
    }

    public String getEmail() {
        return email.toString();
    }

    public String getPhoneNumber() {
        return phoneNumber.toString();
    }

    public String getPassword() {
        return password;
    }

    // SỬA LỖI: Chuyển đổi phương thức abstract cũ thành phương thức thông thường đồng bộ với Server

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return this.getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public boolean isHighestBidder(Auction auction) {
        return auction.getWinner() != null && auction.getWinner().equals(this);
    }

    public boolean isWinner(Auction auction) {
        return (auction.getStatus() == Auction.AuctionStatus.FINISHED || auction.getStatus() == Auction.AuctionStatus.PAID)
                && isHighestBidder(auction);
    }

    public boolean isOwner(Auction auction) {
        return auction.getOwner() != null && auction.getOwner().equals(this);
    }
}