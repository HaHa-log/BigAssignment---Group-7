package Branch;

import Branch.Common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

public abstract class User extends Entity implements Bidder, Seller, AuctionObserver{
    private FullName fullname;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private Balance balance;
    private boolean isAdmin;
    private boolean isBlocked = false;
    private LocalDateTime blockedUntil = null;
    private final ObservableList<String> transactions = FXCollections.observableArrayList();
    private UsersDAO userDatabase = DaoFactory.createUsersDAO();

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        this.fullname = new FullName(firstName, lastName);
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.balance = new Balance(balance); //logic: initial balance is always 0

        //put setPassword at last because this checks on values
        this.setPassword(password);
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil) {
        this.fullname = new FullName(firstName, lastName);
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.balance = new Balance(balance);
        this.setPassword(password);
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.blockedUntil = blockedUntil;
    }

    public void setFirstName(String fstName) {
        this.fullname = new FullName(fstName, fullname.getLastName());
        userDatabase.update(this);
    }

    public void setLastName(String lstName) {
        this.fullname = new FullName(fullname.getFirstName(), lstName);
        userDatabase.update(this);
    }

    public void setEmail(String email) {
        try {
            Email userEmail = new Email(email);
            this.email = userEmail;
            userDatabase.update(this);
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
    }


    public void setPhoneNumber(String number) {
        try {
            PhoneNumber phoneNumber = new PhoneNumber(number);
            this.phoneNumber = phoneNumber;
            userDatabase.update(this);
        } catch (IllegalArgumentException e) {
            e.getMessage();
        }
    }

    public void setPassword(String pass) {
        if (pass.length() >= 6) {
            this.password = pass;
            userDatabase.update(this);
        } else {
            throw new IllegalArgumentException("[Error]: Password must have more than 6 digits");
        }
    }

    public boolean depositMoney(double amount) {
        boolean success = this.balance.deposit(amount);
        if (success) {
            transactions.add("💰 DEPOSIT | +" + amount + " | Balance: " + getBalance());
            userDatabase.update(this);
        }
        return success;
    }

    public boolean withdrawMoney(double amount) {
        boolean success = this.balance.withdraw(amount);
        if (success) {
            transactions.add("💸 WITHDRAW | -" + amount + " | Balance: " + getBalance());
            userDatabase.update(this);
        }
        return success;
    }

    public double getCurrentBalance() {
        return this.balance.showBalance();
    }

    public void setBlocked(LocalDateTime until) {
        this.isBlocked = true;
        this.blockedUntil = until;
        userDatabase.update(this);
    }

    public String getFullName() {
        return fullname.toString();
    }
    public String getFirstName() {
        return fullname.getFirstName();
    }

    public String getLastName() {
        return fullname.getLastName();
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

    public double getBalance() { return balance.showBalance(); }

    public abstract String getRole();

    public abstract boolean isAdmin();

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
        return java.util.Objects.hash(getId());
    }

    public boolean isHighestBidder(Auction auction) {
        return auction.getWinner() != null
                && auction.getWinner().equals(this);
    }

    public boolean isWinner(Auction auction) {
        if (auction.getStatus() == Auction.AuctionStatus.FINISHED || auction.getStatus() == Auction.AuctionStatus.PAID ) {
            if (auction.getWinner().equals(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwner(Auction auction) {
        if (auction.getOwner().equals(this)) {
            return true;
        }
        return false;
    }

    public boolean hasParticipated(Auction auction) {

        for (Bid bid : auction.getBids()) {

            if (bid.getBidder().equals(this)) {
                return true;
            }
        }

        return false;
    }

    public List<AuctionHistoryEntry> getTableHistory(List<Auction> auctions) {
        List<AuctionHistoryEntry> history = new ArrayList<>();

        for (Auction auction : auctions) {
            String state = null;

            if (isOwner(auction)) {
                state = "MY AUCTION";
            } else if (hasParticipated(auction)) {
                if (auction.getRawStatus() == Auction.AuctionStatus.FINISHED ||
                        auction.getRawStatus() == Auction.AuctionStatus.PAID) {
                    state = isWinner(auction) ? "WON" : "LOST";
                } else {
                    state = isHighestBidder(auction) ? "LEADING" : "OUTBID";
                }
            }

            if (state != null) {
                history.add(new AuctionHistoryEntry(
                        auction.getId(),
                        auction.getItem().getName(),
                        auction.getRawStatus().toString(),
                        state
                ));
            }
        }
        return history;
    }

    public List<String> getNotifications(List<Auction> auctions) {

        List<String> notifications = new ArrayList<>();
        for (Auction auction : auctions) {
            boolean participated = false;
            for (User participant : auction.getParticipants()) {

                if (participant.getId() == this.getId()) {
                    participated = true;
                    break;
                }
            }

            if (!participated) {
                continue;
            }

            boolean isWinner = auction.getWinner() != null && auction.getWinner().getId() == this.getId();

            if (auction.getRawStatus() == Auction.AuctionStatus.RUNNING) {
                if (isHighestBidder(auction)) {notifications.add("\uD83D\uDD25 LEADING | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());

                } else {notifications.add("⚠\uFE0F OUTBID | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());}
            }

            if (auction.getRawStatus() == Auction.AuctionStatus.FINISHED) {

                if (isWinner) {notifications.add("\uD83C\uDFC6 WON | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());

                } else {notifications.add("❌ LOST | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());}
            }
        }
        return notifications;
    }

    public ObservableList<String> getTransactions() {
        return transactions;
    }

}
