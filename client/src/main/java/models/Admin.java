package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Member {
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

    public void blockUser(Member member, LocalDateTime until) {
        if (member == null) {
            System.out.println("[Error]: Member not found");
            return;
        }

        member.setBlocked(until);

        System.out.println(
                "[Admin]: Member with ID "
                        + member.getId()
                        + " blocked until "
                        + until
        );
    }

    public void unblockUser(Member member) {
        if (member == null) {
            System.out.println("[Error]: Member not found");
            return;
        }

        member.isUnblocked();

        System.out.println(
                "[Admin]: Member with ID "
                        + member.getId()
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
        return List.of();
    }

    public void printTransactionsByMember(int memberId) {
        List<Transaction> all = getAllTransactions();
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