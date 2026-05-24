package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import repositories.TransactionDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

public class Admin extends User {
    private final UsersDAO userDb = DaoFactory.createUsersDAO();
    private final TransactionDAO transactionDb = DaoFactory.createTransactionDAO();

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
    }

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance, isAdmin, isBlocked, blockedUntil, avatarPath);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public void blockUser(User user, LocalDateTime until) {
        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.setBlocked(until);
        userDb.update(user);

        System.out.println("[Admin]: User with ID " + user.getId() + " blocked until " + until);
    }

    public void unblockUser(User user) {
        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.isUnblocked();
        userDb.update(user);

        System.out.println("[Admin]: User with ID " + user.getId() + " unblocked.");
    }

    public boolean cancelAuction(int auctionId) {
        AuctionManager manager = AuctionManager.getInstance();
        boolean success = manager.cancelAuction(auctionId);
        if (success) {
            System.out.println("[Admin]: Auction " + auctionId + " has been cancelled.");
            return true;
        } else {
            System.out.println("[Admin]: Could not find auction with ID: " + auctionId);
            return false;
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionDb.getAll();
    }

    public void printTransactionsByMember(int memberId) {
        List<Transaction> all = transactionDb.getAll();
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : all) {
            if (transaction.getBuyer().getId() == memberId || transaction.getSeller().getId() == memberId) {
                result.add(transaction);
            }
        }

        if (result.isEmpty()) {
            System.out.println("[System]: No transactions found for member ID: " + memberId);
            return;
        }

        for (Transaction transaction : result) {
            System.out.println(transaction.toString());
        }
    }
}