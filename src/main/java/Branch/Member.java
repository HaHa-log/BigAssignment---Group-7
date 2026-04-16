package Branch;

import java.util.ArrayList;
import java.util.List;

public class Member extends User implements Bidder, Seller {
    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName, lastName, email, phoneNumber,password, balance);
    }

    public List<Transaction> getMyTransactions() {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : Database.getAuctionTransactions()) {
            if (transaction.getBuyer().equals(this) || transaction.getSeller().equals(this)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public void printMyTransactions() {
        List<Transaction> list = getMyTransactions();

        if (list.isEmpty()) {
            System.out.println("[System]: No transactions found.");
            return;
        }

        list.forEach(transaction -> System.out.println(transaction.toString()));
    }
}
