package models;

import models.Common.*;
import repositories.ItemsDAO;
import repositories.TransactionDAO;
import repositories.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Member extends Entity implements Bidder, Seller, AuctionObserver {
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

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        this.fullname = new FullName(firstName, lastName);
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.avatarPath = avatarPath;
        this.balance = new Balance(balance);
        setPassword(password);
    }

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {
        this(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.blockedUntil = blockedUntil;
    }

    public String getFullName() { return fullname.toString(); }
    public String getFirstName() { return fullname.getFirstName(); }
    public String getLastName() { return fullname.getLastName(); }

    public void setFirstName(String fstName) {
        this.fullname = new FullName(fstName, fullname.getLastName());
    }

    public void setLastName(String lstName) {
        this.fullname = new FullName(fullname.getFirstName(), lstName);
    }

    public String getEmail() { return email.toString(); }

    public void setEmail(String email) {
        this.email = new Email(email);
    }

    public String getPhoneNumber() { return phoneNumber.toString(); }

    public void setPhoneNumber(String number) {
        this.phoneNumber = new PhoneNumber(number);
    }

    public String getPassword() { return password; }

    public void setPassword(String pass) {
        if (pass == null || pass.length() < 6) {
            throw new IllegalArgumentException("[Error]: Password must have at least 6 characters.");
        }
        this.password = pass;
    }

    public double getBalance() { return balance.showBalance(); }
    public double getCurrentBalance() { return this.balance.showBalance(); }

    public boolean isAdmin() { return isAdmin; }

    public String getRole() {
        return isAdmin ? "Admin" : "Member";
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

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public List<String> getTransactions() { return transactions; }

    public double getFrozenBalance() { return frozenBalance; }
    public void setFrozenBalance(double frozenBalance) { this.frozenBalance = frozenBalance; }

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

    public void addTransaction(String message) { transactions.add(message); }

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

    public boolean hasParticipated(Auction auction) {
        return auction.getBids().stream().anyMatch(bid -> bid.getBidder().equals(this));
    }

    private boolean isInvolvedIn(Auction auction) {
        return isOwner(auction) || isWinner(auction) || hasParticipated(auction);
    }

    public List<AuctionHistoryEntry> getTableHistory(List<Auction> auctions) {
        List<AuctionHistoryEntry> history = new ArrayList<>();

        for (Auction auction : auctions) {
            String state = null;

            if (isOwner(auction)) {
                state = "MY AUCTION";
            } else if (hasParticipated(auction)) {
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
            if (!isInvolvedIn(auction)) {
                continue;
            }

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

    @Override
    public void onBidPlaced(Auction auction, Bidder bidder, double amount) {
        if (bidder == this) {
            System.out.println("[System]: You have successfully placed a bid of " + amount + " in auction of ID " + auction.getId());
            return;
        }

        String bidderName = ((Member) bidder).getFullName();
        System.out.println("[Notification]: "
                + bidderName + " has bidded " + amount
                + " in auction of ID " + auction.getId()
                + " for " + auction.getItem().getName());
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

    public boolean isEqual(Member other) {
        if (other == null) return false;
        return this.getId() == other.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return this.getId() == member.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}