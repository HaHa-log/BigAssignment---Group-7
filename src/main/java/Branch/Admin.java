package Branch;

import Branch.User;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private UsersDAO userDb = DaoFactory.createUsersDAO();

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName, lastName, email, phoneNumber,password, balance);
    }

    public void blockUser(int userId, LocalDateTime until) {
        User user = userDb.getById(userId);

        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.setBlocked(until);
        System.out.println("[Admin]: User with ID " + user.getId() + " blocked until " + until);
    }

    public void unblockUser(int userId) {
        User user = userDb.getById(userId);

        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.isUnblocked();
        System.out.println("[Admin]: User with ID " + user.getId() + " unblocked.");
    }

    public void cancelAuction(int auctionId) {
        AuctionManager manager = AuctionManager.getInstance();
        boolean success = manager.cancelAuction(auctionId);
        if (success) {
            System.out.println("[Admin]: Auction " + auctionId + " has been cancelled.");
        } else {
            System.out.println("[Admin]: Could not find auction with ID: " + auctionId);
        }
    }

    public List<Transaction> getAllTransactions() {
        return TempDatabase.getAuctionTransactions();
    }

    public void printTransactionsByMember(int memberId) {
        List<Transaction> all = TempDatabase.getAuctionTransactions();
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