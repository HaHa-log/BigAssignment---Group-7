package repositories;

import models.Transaction;

import java.util.List;

public interface TransactionsDAO extends DAO<Transaction> {
    List<Transaction> getByUserId(int userId);

    List<Transaction> getPendingTransactions();

    Transaction getPendingByAuctionAndBuyer(int auctionId, int buyerId);
}
