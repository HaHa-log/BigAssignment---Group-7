package Branch.Exceptions;

public class AuctionClosedException extends CustomisedException {
    public AuctionClosedException(String status) {
        super("Cannot perform the operation because the auction is currently in status: " + status);
    }
}