package services;

import com.group7.dto.auction.*;
import com.group7.dto.item.ItemRequest;
import config.BidWebSocketHandler;
import config.DB;
import config.DbException;
import models.*;
import models.Common.Price;
import models.Exceptions.AuthenticationException;
import models.Exceptions.IllegalTransactionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.BidsDAO;
import repositories.TransactionDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionService {
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();
    private final BidsDAO bidsDAO = DaoFactory.createBidsDAO();
    private final TransactionDAO transactionDb = DaoFactory.createTransactionDAO();
    private final ItemService itemService = new ItemService();
    private final TransactionService transactionService;
    private final BidWebSocketHandler webSocketHandler;

    private final AuctionManager auctionManager = AuctionManager.getInstance();

    public AuctionService(TransactionService transactionService, BidWebSocketHandler webSocketHandler) {
        this.transactionService = transactionService;
        this.webSocketHandler = webSocketHandler;
    }

    public List<AuctionResponse> getAll(int page, int size, String status) {
        List<Auction> pageAuctions = auctionsDAO.getAll(page, size, status);
        return pageAuctions.stream()
                .map(auction -> toResponseWithCachedBids(auction, java.util.Collections.emptyList()))
                .toList();
    }

    public AuctionResponse getById(int id) {
        Auction auction = requireAuction(id);
        finalizeIfExpired(auction);
        return toResponse(auction);
    }

    public AuctionResponse create(CreateAuctionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Error]: Request body is required.");
        }

        Item item = itemService.getDomainItemById(request.getItemId());
        if (item == null) {
            throw new IllegalArgumentException("[Error]: Item not found.");
        }

        User owner = item.getOwner();
        if (owner == null) {
            throw new IllegalArgumentException("[Error]: Item owner is missing.");
        }

        if (owner.isBlocked()) {
            throw new AuthenticationException("Your account is currently blocked and cannot create auctions.");
        }

        Auction auction = owner.createAuction(
                item,
                request.getStartingTime(),
                request.getEndingTime()
        );

        return toResponse(auctionsDAO.getById(auction.getId()));
    }

    public AuctionResponse placeBid(int auctionId, int bidderId, double amount) {
        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Auction auction = auctionsDAO.getByIdWithLock(conn, auctionId);
                if (auction == null) {
                    throw new IllegalArgumentException("[Error]: Auction not found.");
                }
                auction.setBids(bidsDAO.getByAuctionId(auctionId));

                User bidder = usersDAO.getById(bidderId);
                if (bidder == null) {
                    throw new IllegalArgumentException("[Error]: Bidder is invalid.");
                }

                bidder.placeBid(auction, amount);

                auctionsDAO.updateWithConn(conn, auction);
                usersDAO.update(bidder);

                User previousWinner = (User) auction.getWinner();

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        Auction committed = requireAuction(auctionId);
        committed.setBids(bidsDAO.getByAuctionId(auctionId));

        webSocketHandler.broadcastBid(auctionId, committed.getCurrentPrice(), committed.getStatus().name());

        List<AutoBid> autoBids = DaoFactory.createAutoBidDAO().getByAuctionId(auctionId, committed);
        for (AutoBid autoBid : autoBids) {
            if (autoBid.getUser().getId() == bidderId) continue;
            double priceBefore = committed.getCurrentPrice();
            AuctionManager.getInstance().processAutoBids(committed, autoBid);
            double priceAfter = committed.getCurrentPrice();
            if (priceAfter > priceBefore) {
                try (Connection conn = DB.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        auctionsDAO.updateWithConn(conn, committed);
                        conn.commit();
                    } catch (Exception e) {
                        conn.rollback();
                        throw e;
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                }
                webSocketHandler.broadcastBid(auctionId, priceAfter, committed.getStatus().name());
            }
        }

        return toResponseWithCachedBids(committed, committed.getBids());
    }

    public AuctionResponse cancel(int auctionId) {
        Auction auction = requireAuction(auctionId);
        auctionManager.cancelAuction(auctionId);

        return toResponse(auction);
    }

    private void finalizeIfExpired(Auction auction) {
        if (auction.getEndingTime() == null) return;
        if (LocalDateTime.now().isBefore(auction.getEndingTime())) return;
        if (auction.getStatus() != Auction.AuctionStatus.RUNNING) return;

        auction.transitionTo(Auction.AuctionStatus.FINISHED);
        auctionsDAO.update(auction);

        User winner = auction.getWinner();
        if (winner == null) return;

        Transaction existing = DaoFactory.createTransactionDAO()
                .getPendingByAuctionAndBuyer(auction.getId(), winner.getId());
        if (existing != null) return;

        User buyer  = usersDAO.getById(winner.getId());
        User seller = usersDAO.getById(auction.getOwner().getId());
        if (buyer == null || seller == null) return;

        Transaction t = new Transaction(auction, buyer, seller, auction.getCurrentPrice());
        DaoFactory.createTransactionDAO().save(t);
    }

    public AuctionResponse confirmReceipt(int auctionId, int buyerId) {
        User user = usersDAO.getById(buyerId);
        if (user == null) {
            throw new IllegalArgumentException("[Error]: Buyer is invalid.");
        }
        transactionService.confirmReceipt(auctionId, buyerId);
        return toResponse(requireAuction(auctionId));
    }

    @Scheduled(fixedDelay = 5_000)
    public void checkAndCancelExpiredTransactions() {
        auctionManager.checkAndCancelExpiredTransactions();
    }

    @Scheduled(fixedDelay = 1_000)
    public void refreshStatus() {
        List<Auction> allActive = auctionsDAO.getActiveAuctions();
        for (Auction auction : allActive) {
            boolean isChanged = auction.refreshTimedStatus();
            if (isChanged) {
                System.out.println("[SERVICE] Status changed for " + auction.getId() + ". Attempting broadcast...");
                webSocketHandler.broadcastBid(
                        auction.getId(),
                        auction.getCurrentPrice(),
                        auction.getStatus().toString()
                );
                System.out.println("[DEBUG] Auction " + auction.getId() + " transitioned to " + auction.getStatus().name());
            }
        }
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    private AuctionResponse toResponse(Auction auction) {
        List<Bid> bids = java.util.Collections.emptyList();
        if (auction.getId() > 0) {
            bids = bidsDAO.getByAuctionId(auction.getId());
        }
        return toResponseWithCachedBids(auction, bids);
    }

    private AuctionResponse toResponseWithCachedBids(Auction auction, List<Bid> bids) {
        String ownerName = (auction.getOwner() != null) ? auction.getOwner().getFullName() : "Unknown Owner";

        String itemName = "";
        String itemDesc = "";
        String itemImg = "";
        if (auction.getItem() != null) {
            itemName = auction.getItem().getName();
            itemDesc = auction.getItem().getDescription();
            itemImg = auction.getItem().getImagePath();
        }

        User winner = auction.getWinner();

        List<com.group7.dto.bid.BidResponse> bidResponses = java.util.Collections.emptyList();
        if (bids != null && !bids.isEmpty()) {
            bidResponses = bids.stream().map(bid -> new com.group7.dto.bid.BidResponse(
                    bid.getId(),
                    auction.getId(),
                    bid.getBidder() != null ? bid.getBidder().getId() : 0,
                    bid.getBidder() != null ? bid.getBidder().getFullName() : "Unknown",
                    bid.getBidPrice() != null ? bid.getBidPrice().getPrice() : 0.0,
                    bid.getBidTime()
            )).toList();
        }

        return new AuctionResponse(
                auction.getId(),
                auction.getOwnerId(),
                ownerName,
                auction.getItemId(),
                itemName,
                itemDesc,
                itemImg,
                auction.getStatus() != null ? auction.getStatus().name() : "PENDING",
                auction.getStartingPrice(),
                auction.getCurrentPrice(),
                auction.getStartingTime(),
                auction.getEndingTime(),
                winner != null ? winner.getId() : null,
                winner != null ? winner.getFullName() : null,
                bidResponses
        );
    }
}