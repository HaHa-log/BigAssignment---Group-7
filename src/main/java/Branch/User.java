package Branch;

import Branch.Common.FullName;
import Branch.Common.Email;
import Branch.Common.PhoneNumber;
import Branch.Common.Balance;

import java.time.LocalDateTime;

public abstract class User extends Entity {
    private FullName fullName;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private Balance balance;
    private boolean isAdmin;
    private boolean isBlocked = false;

    public User(int id, FullName fullName, Email email, PhoneNumber phoneNumber, String password, Balance balance) {
        super(id, fullName.toString());
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.setPassword(password);
        this.balance = balance;
    }

    public User(FullName fullName, Email email, PhoneNumber phoneNumber, String password, Balance balance) {
        super(fullName.toString());
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.setPassword(password);
        this.balance = balance;
    }

    public void setPassword(String pass) {
        //Coder's note: Cân nhắc đủ kí tự đặc biệt
        if (pass.length() >= 6) {
            this.password = pass;
        } else {
            throw new IllegalArgumentException("[Error]: Password must have more than 6 digits");
        }
    }

    public boolean depositMoney(double amount) {
        return this.balance.deposit(amount);
    }

    public boolean withdrawMoney(double amount) {
        return this.balance.withdraw(amount);
    }

    public double getCurrentBalance() {
        return this.balance.showBalance();
    }

    public String getFirstName() {
        return fullName.getFirstName();
    }

    public String getLastName() {
        return fullName.getLastName();
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

    public double getBalance() {
        return balance.showBalance();
    }

    public abstract boolean isAdmin();

    public abstract boolean isBlocked();
}
