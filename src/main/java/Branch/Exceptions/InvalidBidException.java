package Branch.Exceptions;

public class InvalidBidException extends Exception {
    public InvalidBidException(double currentPrice, double offeredPrice) {
        super("The offered price of " + offeredPrice + " has to be greater than the current price of " + currentPrice + "!");
    }
}