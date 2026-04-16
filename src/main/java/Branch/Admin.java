package Branch;

import Branch.User;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    public Admin(int id, String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(id, firstName, lastName, email, phoneNumber,password, balance);
    }

    public void blockUser(int userId) {}

    public void cancelAuction(int auctionId) {}

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