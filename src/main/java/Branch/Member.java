package Branch;

import java.util.ArrayList;
import java.util.List;

public class Member extends User implements Bidder, Seller, AuctionObserver {
    private List<Item> inventory;

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(firstName, lastName, email, phoneNumber, password, balance);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    @Override
    public void onBidPlaced(Auction auction, Bidder bidder, double amount) {
        if (bidder == this) {
            return;
        }

        String bidderName = ((User) bidder).getFullName();
        System.out.println("[Notification]: "
                + bidderName + " has bidded " + amount
                + " in auction of ID " + auction.getId()
                + " for " + auction.getItem().getName());
    }

    public void addItem(Item item) {
        this.inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public List<Transaction> getMyTransactions() {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : TempDatabase.getAuctionTransactions()) {
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
