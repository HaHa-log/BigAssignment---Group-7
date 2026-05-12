package Branch;

import model.TransactionDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Member extends User implements Bidder, Seller, AuctionObserver {
    private List<Item> inventory;

    private TransactionDAO transactionDb = DaoFactory.createTransactionDAO();

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName, lastName, email, phoneNumber, password, balance);
    }

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil) {
        super(firstName, lastName, email, phoneNumber, password, balance, isAdmin, isBlocked, blockedUntil);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    //Observers
    @Override
    public String onBidPlaced(Auction auction, Bid bid) {
        if (bid.getBidder() == this) {
            return "";
        }

        String bidderName = ((User) bid.getBidder()).getFullName();
        String notification = ("[Notification]: "
                + bidderName + " has bidded " + bid.getBidPrice()
                + " in auction of ID " + auction.getId()
                + " for " + auction.getItem().getName());
       return notification;
    }

    public void addItem(Item item) {
        this.inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public List<Transaction> getMyTransactions() {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactionDb.getAll()) {
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
