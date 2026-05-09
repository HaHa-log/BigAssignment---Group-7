package Branch;

import Branch.Common.Balance;
import Branch.Common.Email;
import Branch.Common.FullName;
import Branch.Common.PhoneNumber;
import Branch.Common.ParticipationDetails;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

public abstract class User extends Entity {
    private FullName fullname;
    private Email email;
    private PhoneNumber phoneNumber;
    private String password;
    private Balance balance;
    private boolean isAdmin;
    private boolean isBlocked = false;
    private LocalDateTime blockedUntil = null;

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
            userDatabase.update(this);
        }
        return success;
    }

    public boolean withdrawMoney(double amount) {
        boolean success = this.balance.withdraw(amount);
        if (success) {
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

    public boolean isEqual(User this, User other) {
        boolean result = false;
        if (this.getId() == other.getId()) {
            result = true;
        }
        return result;
    }

    public List<String> getAuctionHistory(List<Auction> auctions) {
        List<String> history = new ArrayList<>();

        for (Auction auction : auctions) {
            boolean isOwner = auction.getOwner().getId() == this.getId();

            if (isOwner) {history.add("MY AUCTION | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());}

            if (auction.getStatus() != Auction.AuctionStatus.FINISHED) {continue;}

            boolean participated = auction.getParticipants().contains(this);

            boolean isWinner = auction.getWinner() != null &&auction.getWinner().getId()== this.getId();

            if (isWinner) {history.add("WON | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());}

            else if (participated) {history.add("LOST | " + auction.getItem().getName() + " | " + auction.getCurrentPrice());}

        }
        return history;
    }
}
