package models.Exceptions;

public class InvalidBidException extends CustomisedException {
    public InvalidBidException(double currentPrice, double offeredPrice) {
        super("The offered price of " + offeredPrice + " has to be greater than the current price of " + currentPrice + "!");
    }
}