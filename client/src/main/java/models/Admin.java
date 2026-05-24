package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
        setAdmin(true);
    }

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance, isAdmin, isBlocked, blockedUntil, avatarPath);
        setAdmin(true);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    // ĐỒNG BỘ DTO: Đổi tham số từ Member cũ sang lớp User phẳng mới để tránh lỗi ClassCastException
    public void blockUser(User targetUser, LocalDateTime until) {
        if (targetUser == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        targetUser.setBlocked(until);

        System.out.println(
                "[Admin]: User with ID "
                        + targetUser.getId()
                        + " blocked until "
                        + until
        );
    }

    public void unblockUser(User targetUser) {
        if (targetUser == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        targetUser.isUnblocked();

        System.out.println(
                "[Admin]: User with ID "
                        + targetUser.getId()
                        + " unblocked."
        );
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
        return new ArrayList<>();
    }

    public void printTransactionsByUser(int userId, List<Transaction> allTransactions) {
        if (allTransactions == null || allTransactions.isEmpty()) {
            System.out.println("[System]: No transactions found.");
            return;
        }

        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            if ((transaction.getBuyer() != null && transaction.getBuyer().getId() == userId) ||
                    (transaction.getSeller() != null && transaction.getSeller().getId() == userId)) {
                result.add(transaction);
            }
        }

        if (result.isEmpty()) {
            System.out.println("[System]: No transactions found for user ID: " + userId);
            return;
        }

        for (Transaction transaction : result) {
            System.out.println(transaction.toString());
        }
    }
}
