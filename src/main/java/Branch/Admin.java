package Branch;

import Branch.User;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    public Admin(String firstName, String lastName, String email, String phoneNumber, String password) {
        super(firstName, lastName, email, phoneNumber,password, 0.0);
    }

    public void blockUser(int userId) {
        List<User> userList = Database.getAllUsers();
        for (User user: userList) {
            if (user.getId() == userId) {
                userList.remove(userId);
                System.out.println("[Admin]: User with ID ");
            }
        }
    }

    public void cancelAuction(int auctionId, AuctionManager manager) {
        boolean success = manager.cancelAuction(auctionId);
        if (success) {
            System.out.println("[Admin]: Auction " + auctionId + " has been cancelled.");
        } else {
            System.out.println("[Admin]: Could not find auction with ID: " + auctionId);
        }
    }

    public List<Transaction> getAllTransactions() {
        return Database.getAuctionTransactions();
    }

    public void printTransactionsByMember(int memberId) {
        List<Transaction> all = Database.getAuctionTransactions();
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