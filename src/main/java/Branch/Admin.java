package Branch;

import Branch.Common.FullName;
import Branch.Common.Email;
import Branch.Common.PhoneNumber;
import Branch.Common.Balance;
import java.time.LocalDateTime;
import java.util.List;

public class Admin extends User {
    public Admin(int id, FullName fullName, Email email, PhoneNumber phoneNumber, String password) {
        super(id, fullName, email, phoneNumber, password, new Balance(0.0));
    }

    public Admin(FullName fullName, Email email, PhoneNumber phoneNumber, String password) {
        super(fullName, email, phoneNumber, password, new Balance(0.0));
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    public void blockUser(int userId, LocalDateTime until) {
        User user = TempDatabase.getUserById(userId);

        if (user instanceof Member member) {
            member.setBlocked(until);
            System.out.println("[Admin]: User ID " + user.getId() + " blocked until " + until);
        } else if (user instanceof Admin) {
            System.out.println("[Error]: Cannot block another Admin!");
        } else {
            System.out.println("[Error]: User not found");
        }
    }

    public void unblockUser(int userId) {
        User user = TempDatabase.getUserById(userId);

        if (user instanceof Member member) {
            member.unblock();
            System.out.println("[Admin]: User ID " + user.getId() + " unblocked.");
        } else {
            System.out.println("[Error]: User not found or not a Member");
        }
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
        User user = TempDatabase.getUserById(memberId);
        if (!(user instanceof Member member)) {
            System.out.println("[Error]: Member not found.");
            return;
        }

        List<Transaction> list = member.getMyTransactions();

        if (list.isEmpty()) {
            System.out.println("[System]: No transactions found.");
        } else {
            list.forEach(transaction -> System.out.println(transaction.toString()));
        }
    }
}