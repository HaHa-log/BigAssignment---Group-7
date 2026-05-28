package repositories;

import models.Transaction;

import java.util.List;

public interface TransactionDAO extends DAO<Transaction> {
    List<Transaction> getByUserId(int userId);

    List<Transaction> getPendingTransactions();

    Transaction getPendingByAuctionAndBuyer(int auctionId, int buyerId);
}
