package Branch;

import Branch.Common.FullName;
import Branch.Common.Email;
import Branch.Common.PhoneNumber;
import Branch.Common.Balance;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Member extends User implements Bidder, Seller, AuctionObserver {
    private LocalDateTime blockedUntil = null;
    private List<Item> inventory = new ArrayList<>();

    public Member(int id, FullName fullName, Email email, PhoneNumber phoneNumber, String password, Balance balance) {
        super(id, fullName, email, phoneNumber, password, balance);
    }

    public Member(FullName fullName, Email email, PhoneNumber phoneNumber, String password, Balance balance) {
        super(fullName, email, phoneNumber, password, balance);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    void setBlocked(LocalDateTime until) {
        this.blockedUntil = until;
    }

    void unblock() {
        this.blockedUntil = null;
    }

    @Override
    public boolean isBlocked() {
        return blockedUntil != null && LocalDateTime.now().isBefore(blockedUntil);
    }

    @Override
    public void onBidPlaced(Auction auction, Bidder bidder, double amount) {
        if (bidder == this) {
            return;
        }

        String bidderName = ((User) bidder).getName();
        System.out.println("[Notification]: "
                + bidderName + " has bidded " + amount
                + " in auction of ID " + auction.getAuctionId()
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
