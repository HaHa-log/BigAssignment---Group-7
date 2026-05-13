package model;

import Branch.Transaction;

import java.util.List;

public interface TransactionDAO extends DAO<Transaction> {
    List<Transaction> getByUserId(int userId);
}
