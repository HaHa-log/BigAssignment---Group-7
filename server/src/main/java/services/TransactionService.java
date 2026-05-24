package services;

import com.group7.dto.transaction.TransactionResponse;
import config.DbException;
import models.*;
import models.Exceptions.IllegalTransactionException;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.TransactionDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionDAO transactionDAO = DaoFactory.createTransactionDAO();
    private final AuctionsDAO auctionsDAO       = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO             = DaoFactory.createUsersDAO();

    // auction kết thúc  → tạo PENDING transaction và lưu DB
    public TransactionResponse createPendingTransaction(int auctionId) {
        Auction auction = requireAuction(auctionId);

        Member winner = auction.getWinner();
        if (winner == null) {
            throw new IllegalArgumentException("[Error]: Auction has no winner.");
        }
        if (!(winner instanceof Member buyer)) {
            throw new IllegalArgumentException("[Error]: Winner is not a valid Member.");
        }

        Transaction existing = transactionDAO.getPendingByAuctionAndBuyer(auctionId, buyer.getId());
        if (existing != null) {
            return toResponse(existing);
        }

        Member seller = auction.getOwner();
        double finalPrice = auction.getCurrentPrice();

        Transaction transaction = new Transaction(auction, buyer, seller, finalPrice);
        transactionDAO.save(transaction);
        return toResponse(transaction);
    }
    //buyer confirm
    public TransactionResponse confirmReceipt(int auctionId, int buyerId) {
        Member buyer = usersDAO.getById(buyerId);
        if (!(buyer instanceof Member member)) {
            throw new IllegalArgumentException("[Error]: Buyer is invalid.");
        }

        Transaction transaction = transactionDAO.getPendingByAuctionAndBuyer(auctionId, member.getId());
        if (transaction == null) {
            throw new IllegalArgumentException("[Error]: No pending transaction found for this auction.");
        }

        try {
            transaction.markCompleted(); // logic freeze/deduct/deposit đã có sẵn trong model
        } catch (IllegalTransactionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        transactionDAO.update(transaction);

        Auction auction = transaction.getAuction();
        auction.transitionTo(Auction.AuctionStatus.PAID);
        auctionsDAO.update(auction);

        return toResponse(transaction);
    }

    public TransactionResponse refundTransaction(int transactionId) {
        Transaction transaction = transactionDAO.getById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("[Error]: Transaction not found.");
        }

        try {
            transaction.markRefunded();
        } catch (IllegalTransactionException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        transactionDAO.update(transaction);
        return toResponse(transaction);
    }

    public List<TransactionResponse> getByUserId(int userId) {
        return transactionDAO.getByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getById(int transactionId) {
        Transaction t = transactionDAO.getById(transactionId);
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
