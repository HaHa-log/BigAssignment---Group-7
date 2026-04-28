package Branch.Common;

public class Balance {
    private double balance;

    public Balance(double initialAmount) {
        if (initialAmount < 0) {
            throw new IllegalArgumentException("[Error]: Initial amount cannot be negative!");
        }
        this.balance = initialAmount;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("[Error]: Deposited amount must be greater than 0");
        }
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("[Error]: Withdrawn amount must be greater than 0");
        }
        if (amount > balance) {
            System.out.println("[Error]: Account does not have enough money");
            return false;
        }
        this.balance -= amount;
        return true;
    }

    public double showBalance() {
        return balance;
    }
}