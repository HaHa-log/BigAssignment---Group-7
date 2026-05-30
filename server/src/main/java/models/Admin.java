package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import repositories.TransactionDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Admin extends User {
    private static final Logger logger = LoggerFactory.getLogger(Admin.class);

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
            logger.warn("[Error]: User not found");
            return;
        }

        user.setBlocked(until);
        userDb.update(user);

        logger.info("[Admin]: User with ID {} blocked until {}", user.getId(), until);
    }

    public void unblockUser(User user) {
        if (user == null) {
            logger.warn("[Error]: User not found");
            return;
        }

        user.isUnblocked();
        userDb.update(user);

        logger.info("[Admin]: User with ID {} unblocked.", user.getId());
    }

    public boolean cancelAuction(int auctionId) {
        AuctionManager manager = AuctionManager.getInstance();
        boolean success = manager.cancelAuction(auctionId);
        if (success) {
            logger.info("[Admin]: Auction {} has been cancelled.", auctionId);
            return true;
        } else {
            logger.info("[Admin]: Could not find auction with ID: {}", auctionId);
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
            logger.info("[System]: No transactions found for member ID: {}", memberId);
            return;
        }

        for (Transaction transaction : result) {
            logger.info(transaction.toString());
        }
    }
}