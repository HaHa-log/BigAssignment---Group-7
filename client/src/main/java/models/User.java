package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Common.Balance;
import models.Common.AuctionAlert;
import models.Common.AuctionHistoryEntry;
import models.Common.Email;
import models.Common.FullName;
import models.Common.NotificationType;
import models.Common.PhoneNumber;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class User extends Entity implements Bidder, Seller, AuctionObserver {
    private FullName fullname;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private Balance balance;
    private boolean isAdmin;
    private boolean isBlocked = false;
    private LocalDateTime blockedUntil = null;
    private String avatarPath;
    private final ObservableList<String> transactions = FXCollections.observableArrayList();
    //private final UsersDAO userDatabase = DaoFactory.createUsersDAO();
    private double frozenBalance = 0;

    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        this.fullname = new FullName(firstName, lastName);
        this.email = new Email(email);
        this.phoneNumber = new PhoneNumber(phoneNumber);
        this.avatarPath = avatarPath;
        this.balance = new Balance(balance);

        // Enforce password logic consistently
        setPassword(password);
    }

    // DB hydration constructor
    public User(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {
        this(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        this.blockedUntil = blockedUntil;
    }

    public void setFirstName(String fstName) {
        this.fullname = new FullName(fstName, fullname.getLastName());
        //update();
    }

    public void setLastName(String lstName) {
        this.fullname = new FullName(fullname.getFirstName(), lstName);
        //update();
    }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    //public void update() {
    //userDatabase.update(this);
    //}

    public void setEmail(String email) {
        this.email = new Email(email);
        //update();
    }

    public void setPhoneNumber(String number) {
        this.phoneNumber = new PhoneNumber(number);
        //update();
    }

    public void setPassword(String pass) {
        if (pass == null || pass.length() < 6) {
            throw new IllegalArgumentException("[Error]: Password must have at least 6 characters.");
        }
        this.password = pass;
    }

    public boolean depositMoney(double amount) {
        boolean success = this.balance.deposit(amount);
        if (success) {
            //update();
        }
        return success;
    }

    public boolean withdrawMoney(double amount) {
        boolean success = this.balance.withdraw(amount);
        if (success) {
            //update();
        }
        return success;
    }

    public boolean freezeMoney(double amount) {
        if (amount <= 0) {return false;}
        if (this.balance.showBalance() < amount) {return false;}

        boolean success = this.balance.withdraw(amount);
        if (!success) {return false;}

        this.frozenBalance += amount;

        //update();
        return true;
    }

    public boolean spendFrozenMoney(double amount) {
        if (amount <= 0) {return false;}
        if (frozenBalance < amount) {return false;}

        frozenBalance -= amount;

        //update();
        return true;
    }

    public boolean unfreezeMoney(double amount) {
        if (amount <= 0) {return false;}
        if (frozenBalance < amount) {return false;}

        frozenBalance -= amount;
        balance.deposit(amount);

        //update();
        return true;
    }

    public double getCurrentBalance() { return this.balance.showBalance(); }
    public double getBalance() { return balance.showBalance(); }
    public double getFrozenBalance() { return frozenBalance; }
    public void setFrozenBalance(double frozenBalance) { this.frozenBalance = frozenBalance; }

    public void setBlocked(LocalDateTime until) {
        this.isBlocked = true;
        this.blockedUntil = until;
        //update();
    }

    public boolean isBlocked() {
        if (isBlocked && blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil)) {
            this.isBlocked = false;
            this.blockedUntil = null;
            //update();
        }
        return isBlocked;
    }

    public void isUnblocked() {
        this.isBlocked = false;
        this.blockedUntil = null;
        //update();
    }

    public String getFullName() { return fullname.toString(); }
    public String getFirstName() { return fullname.getFirstName(); }
    public String getLastName() { return fullname.getLastName(); }
    public String getEmail() { return email.toString(); }
    public String getPhoneNumber() { return phoneNumber.toString(); }
    public String getPassword() { return password; }

    public abstract String getRole();
    public abstract boolean isAdmin();

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

    public boolean isHighestBidder(Auction auction) {
        return auction.getWinner() != null && auction.getWinner().equals(this);
    }

    public boolean isWinner(Auction auction) {
        return (auction.getStatus() == Auction.AuctionStatus.FINISHED || auction.getStatus() == Auction.AuctionStatus.PAID)
                && isHighestBidder(auction);
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

    public ObservableList<String> getTransactions() { return transactions; }

    public void addTransaction(String message) {transactions.add(message);}
}