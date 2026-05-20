package models.Exceptions;

public class IllegalTransactionException extends CustomisedException {
    public IllegalTransactionException(String message) {
        super(message);
    }
}