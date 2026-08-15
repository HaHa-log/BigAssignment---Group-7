package services;

import com.group7.dto.transaction.TransactionResponse;
import models.*;
import models.Exceptions.IllegalTransactionException;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.TransactionsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionsDAO transactionsDAO = DaoFactory.createTransactionDAO();
    private final AuctionsDAO auctionsDAO       = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO             = DaoFactory.createUsersDAO();

    // auction kết thúc  → tạo PENDING transaction và lưu DB
    public TransactionResponse createPendingTransaction(int auctionId) {
        Auction auction = requireAuction(auctionId);

        User winner = auction.getWinner();
        if (winner == null) {
            throw new IllegalArgumentException("[Error]: Auction has no winner.");
        }

        Transaction existing = transactionsDAO.getPendingByAuctionAndBuyer(auctionId, winner.getId());
        if (existing != null) {
            return toResponse(existing);
        }

        User seller = auction.getOwner();
        double finalPrice = auction.getCurrentPrice();

        Transaction transaction = new Transaction(auction, winner, seller, finalPrice);
        transactionsDAO.save(transaction);
        return toResponse(transaction);
    }
    //buyer confirm
    public TransactionResponse confirmReceipt(int auctionId, int buyerId) {
        User buyer = usersDAO.getById(buyerId);
        if (buyer == null) {
            throw new IllegalArgumentException("[Error]: Buyer is invalid.");
        }

        Auction auction = requireAuction(auctionId);
        User winner = auction.getWinner();
        if (winner == null || winner.getId() != buyerId) {
            throw new IllegalArgumentException("[Error]: You are not the winner of this auction.");
        }

        Transaction transaction = transactionsDAO.getPendingByAuctionAndBuyer(auctionId, buyerId);
        if (transaction == null) {
            User seller = usersDAO.getById(auction.getOwner().getId());
            transaction = new Transaction(auction, buyer, seller, auction.getCurrentPrice());
            transactionsDAO.save(transaction);
        }

        try {
            transaction.markCompleted(); // spendFrozenMoney + depositMoney seller
        } catch (IllegalTransactionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        usersDAO.update(transaction.getBuyer());
        usersDAO.update(transaction.getSeller());
        transactionsDAO.update(transaction);

        auction.transitionTo(Auction.AuctionStatus.PAID);
        auctionsDAO.update(auction);

        return toResponse(transaction);
    }

    public TransactionResponse refundTransaction(int transactionId) {
        Transaction transaction = transactionsDAO.getById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("[Error]: Transaction not found.");
        }

        try {
            transaction.markExpiredRefund();
        } catch (IllegalTransactionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        transactionsDAO.update(transaction);
        return toResponse(transaction);
    }

    public List<TransactionResponse> getByUserId(int userId) {
        return transactionsDAO.getByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getById(int transactionId) {
        Transaction t = transactionsDAO.getById(transactionId);
        if (t == null) {
            throw new IllegalArgumentException("[Error]: Transaction not found.");
        }
        return toResponse(t);
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getTransactionId(),
                t.getAuction().getId(),
                t.getAuction().getItem() != null ? t.getAuction().getItem().getName() : "",
                t.getBuyer().getId(),
                t.getBuyer().getFullName(),
                t.getSeller().getId(),
                t.getSeller().getFullName(),
                t.getFinalAmount(),
                t.getStatus().name(),
                t.getPaidAt(),
                t.getCompletedAt(),
                t.getExpiryTime()
        );
    }
}
