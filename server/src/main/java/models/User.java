package models;

import models.Common.*;
import repositories.ItemsDAO;
import repositories.TransactionDAO;
import repositories.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final List<String> transactions = new ArrayList<>();
    private double frozenBalance = 0;

    private final TransactionDAO transactionDb = DaoFactory.createTransactionDAO();
    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();

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

    public String getFullName() { return fullname.toString(); }
    public String getFirstName() { return fullname.getFirstName(); }
    public String getLastName() { return fullname.getLastName(); }

    public String getEmail() { return email.toString(); }

    public void setEmail(String email) {
        this.email = new Email(email);
    }

    public String getPhoneNumber() {
        return phoneNumber.toString();
    }

    public String getPassword() { return password; }

    public void setPassword(String pass) {
        if (pass == null || pass.length() < 6) {
            throw new IllegalArgumentException("[Error]: Password must have at least 6 characters.");
        }
        this.password = pass;
    }

    public double getBalance() {
        return balance.showBalance();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getRole() {
        return isAdmin ? "Admin" : "User";
    }

    public boolean isBlocked() {
        if (isBlocked && blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil)) {
            this.isBlocked = false;
            this.blockedUntil = null;
        }
        return isBlocked;
    }

    public void setBlocked(LocalDateTime until) {
        this.isBlocked = true;
        this.blockedUntil = until;
    }

    public void isUnblocked() {
        this.isBlocked = false;
        this.blockedUntil = null;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public double getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(double frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public boolean depositMoney(double amount) {
        return this.balance.deposit(amount);
    }

    public boolean withdrawMoney(double amount) {
        return this.balance.withdraw(amount);
    }

    public boolean freezeMoney(double amount) {
        if (amount <= 0) return false;
        if (this.balance.showBalance() < amount) return false;

        boolean success = this.balance.withdraw(amount);
        if (!success) return false;

        this.frozenBalance += amount;
        return true;
    }

    public boolean spendFrozenMoney(double amount) {
        if (amount <= 0) return false;
        if (frozenBalance < amount) return false;

        frozenBalance -= amount;
        return true;
    }

    public boolean unfreezeMoney(double amount) {
        if (amount <= 0) return false;
        if (frozenBalance < amount) return false;

        frozenBalance -= amount;
        balance.deposit(amount);
        return true;
    }

    public boolean isHighestBidder(Auction auction) {
        return auction != null
                && auction.getWinner() != null
                && this.getId() == auction.getWinner().getId();
    }

    public boolean isWinner(Auction auction) {
        return isHighestBidder(auction)
                && (auction.getStatus() == Auction.AuctionStatus.FINISHED || auction.getStatus() == Auction.AuctionStatus.PAID);
    }

    public boolean isOwner(Auction auction) {
        return auction.getOwner().equals(this);
    }

    public List<AuctionHistoryEntry> getTableHistory(List<Auction> auctions) {
        List<AuctionHistoryEntry> history = new ArrayList<>();

        for (Auction auction : auctions) {
            String state;

            if (isOwner(auction)) {
                state = "MY AUCTION";
            } else {
                if (auction.getStatus() == Auction.AuctionStatus.FINISHED ||
                        auction.getStatus() == Auction.AuctionStatus.PAID) {
                    state = isWinner(auction) ? "WON" : "LOST";
                } else {
                    state = isHighestBidder(auction) ? "LEADING" : "OUTBID";
                }
            }

            if (state != null) {
                history.add(new AuctionHistoryEntry(
                        auction.getId(),
                        auction.getItem().getName(),
                        auction.getStatus().toString(),
                        state
                ));
            }
        }
        return history;
    }

    public List<AuctionAlert> getNotifications(List<Auction> auctions) {
        List<AuctionAlert> alerts = new ArrayList<>();

        for (Auction auction : auctions) {

            boolean isWinner = auction.getWinner() != null && auction.getWinner().getId() == this.getId();

            if (auction.getStatus() == Auction.AuctionStatus.RUNNING) {
                if (isOwner(auction)) {
                    alerts.add(new AuctionAlert(NotificationType.MY_AUCTION_RUNNING, auction));
                } else if (isHighestBidder(auction)) {
                    alerts.add(new AuctionAlert(NotificationType.LEADING, auction));
                } else {
                    alerts.add(new AuctionAlert(NotificationType.OUTBID, auction));
                }
            }

            if (auction.getStatus() == Auction.AuctionStatus.FINISHED ||
                    auction.getStatus() == Auction.AuctionStatus.PAID) {
                if (isOwner(auction)) {
                    alerts.add(new AuctionAlert(NotificationType.MY_AUCTION_FINISHED, auction));
                } else if (isWinner) {
                    alerts.add(new AuctionAlert(NotificationType.WON, auction));
                } else {
                    alerts.add(new AuctionAlert(NotificationType.LOST, auction));
                }
            }
        }
        return alerts;
    }

    public void addItem(Item item) {
        if (item != null) {
            item.setOwner(this);
            if (item.getId() > 0) {
                itemsDb.update(item);
            }
            System.out.println("[System]: Item " + item.getName() + " is now owned by " + this.getFullName());
        }
    }

    public void removeItem(Item item) {
        System.out.println("[System]: Item " + item.getName() + " removed from " + this.getFullName() + "'s inventory.");
    }

    public List<Item> getInventory() {
        return itemsDb.getByOwnerId(this.getId());
    }

    public List<Transaction> getMyTransactions() {
        return transactionDb.getByUserId(this.getId());
    }

    public void printMyTransactions() {
        List<Transaction> list = getMyTransactions();

        if (list.isEmpty()) {
            System.out.println("[System]: No transactions found.");
            return;
        }

        list.forEach(transaction -> System.out.println(transaction.toString()));
    }

    public boolean isEqual(User other) {
        if (other == null) return false;
        return this.getId() == other.getId();
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
}