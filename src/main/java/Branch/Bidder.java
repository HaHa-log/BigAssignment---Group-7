package Branch;

public interface Bidder {
    double getBalance();

    default boolean placeBid(Auction auction, double amount) {
        if (this instanceof User && ((User) this).isBlocked()) {
            System.out.println("[Error]: Your account is blocked");
            return false;
        }

        if (amount > getBalance()) {
            System.out.println("[Error]: Bid cannot be greater than your balance");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[Error]: Bid must be greater than zero");
            return false;
        }

        return auction.placeBid(this, amount);
    }
}