package Branch;

public interface Bidder {
    double getBalance();

    default boolean placeBid(Auction auction, double amount) throws Exception{
        if (this instanceof User && ((User) this).isBlocked()) {
            throw new IllegalArgumentException("[Error]: Your account is blocked");
        }

        if (amount > ((User) this).getBalance()) {
            throw new IllegalArgumentException("Bid cannot be greater than your balance");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("[Error]: Bid must be greater than zero");
        }

        return true;
    }
}