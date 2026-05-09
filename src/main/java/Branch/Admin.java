package Branch;

import Branch.User;
import model.TransactionDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admin extends Member {
    private UsersDAO userDb = DaoFactory.createUsersDAO();
    private TransactionDAO transactionDb = DaoFactory.createTransactionDAO();

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName, lastName, email, phoneNumber,password, balance);
    }

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil) {
        super(firstName, lastName, email, phoneNumber, password, balance, isAdmin, isBlocked, blockedUntil);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    //requires for block/active toggle button
    //the button only changes ìf the value in the actual object changes
    public void blockUser(User user, LocalDateTime until) {

        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.setBlocked(until);
        userDb.update(user);

        System.out.println(
                "[Admin]: User with ID "
                        + user.getId()
                        + " blocked until "
                        + until
        );
    }

    public void unblockUser(User user) {

        if (user == null) {
            System.out.println("[Error]: User not found");
            return;
        }

        user.isUnblocked();
        userDb.update(user);

        System.out.println(
                "[Admin]: User with ID "
                        + user.getId()
                        + " unblocked."
        );
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