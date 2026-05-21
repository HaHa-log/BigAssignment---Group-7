package models;
import java.time.LocalDateTime;
import java.util.List;
import repositories.ItemsDAO;
import repositories.TransactionDAO;
import repositories.impl.DaoFactory;

public class Member extends User implements Bidder, Seller, AuctionObserver {
    private final TransactionDAO transactionDb = DaoFactory.createTransactionDAO();
    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance,String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance,avatarPath);
    }

    public Member(String firstName, String lastName, String email, String phoneNumber, String password, double balance, boolean isAdmin, boolean isBlocked, LocalDateTime blockedUntil, String avatarPath) {

        super(firstName, lastName, email, phoneNumber, password, balance, isAdmin, isBlocked, blockedUntil,avatarPath);
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
            System.out.println("[System]: You have successfully placed a bid of " + amount + " in auction of ID " + auction.getId());
            return;
        }

        String bidderName = ((User) bidder).getFullName();
        System.out.println("[Notification]: "
                + bidderName + " has bidded " + amount
                + " in auction of ID " + auction.getId()
                + " for " + auction.getItem().getName());
    }

    public void addItem(Item item) {
        if (item != null) {
            item.setOwnerId(this.getId());
            if (item.getId() > 0) {
                itemsDb.update(item);
            }
            System.out.println("[System]: Item " + item.getName() + " is now owned by " + this.getFullName());
        }
    }

    public void removeItem(Item item) {
        System.out.println("[System]: Item " + item.getName() + " removed from " + this.getFullName() + "'s inventory.");
    }
    public List<Item> getInventory() {
        return itemsDb.getByOwnerId(this.getId());
    }


    public List<Transaction> getMyTransactions() {
        return transactionDb.getByUserId(this.getId());    }

    public void printMyTransactions() {
        List<Transaction> list = getMyTransactions();

        if (list.isEmpty()) {
            System.out.println("[System]: No transactions found.");
            return;
        }

        list.forEach(transaction -> System.out.println(transaction.toString()));
    }
}
