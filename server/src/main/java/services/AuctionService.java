package services;

import com.group7.dto.auction.*;
import com.group7.dto.item.ItemRequest;
import config.BidWebSocketHandler;
import models.*;
import models.Common.Price;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.BidsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class AuctionService {
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();
    private final BidsDAO bidsDAO = DaoFactory.createBidsDAO();
    private final ItemService itemService = new ItemService();
    private final TransactionService transactionService;
    private final BidWebSocketHandler webSocketHandler;

    private final AuctionManager auctionManager = AuctionManager.getInstance();

    public AuctionService(TransactionService transactionService,BidWebSocketHandler webSocketHandler) {
        this.transactionService = transactionService;
        this.webSocketHandler = webSocketHandler;
    }

    public List<AuctionResponse> getAll(int page, int size,String status) {
        List<Auction> pageAuctions = auctionsDAO.getAll(page, size, "ALL");

        return pageAuctions.stream()
                .map(auction -> toResponseWithCachedBids(auction, java.util.Collections.emptyList()))
                .toList();
    }

    public AuctionResponse getById(int id) {
        Auction auction = requireAuction(id);
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

        Auction auction = auctionManager.createAuction(
                owner,
                item,
                request.getStartingTime(),
                request.getEndingTime()
        );

        return toResponse(auctionsDAO.getById(auction.getId()));
    }

    public AuctionResponse placeBid(int auctionId, int bidderId, double amount) {
        Auction auction = requireAuction(auctionId);
        User bidder = usersDAO.getById(bidderId);
        if (bidder == null) {
            throw new IllegalArgumentException("[Error]: Bidder is invalid.");
        }
        bidder.placeBid(auction, amount);

        Bid bid = new Bid(auction, bidder, new Price(amount));
        bidsDAO.save(bid);

        models.AuctionManager.getInstance().processAutoBids(auction, null);

        webSocketHandler.broadcastBid(auctionId, auction.getCurrentPrice());

        return toResponse(auctionsDAO.getById(auctionId));
    }

    public AuctionResponse cancel(int auctionId) {
        Auction auction = requireAuction(auctionId);
        auction.transitionTo(Auction.AuctionStatus.CANCELED);
        auctionsDAO.update(auction);
        return toResponse(auction);
    }

    public AuctionResponse confirmReceipt(int auctionId, int buyerId) {
        User user = usersDAO.getById(buyerId);
        if (user == null) {
            throw new IllegalArgumentException("[Error]: Buyer is invalid.");
        }

        transactionService.confirmReceipt(auctionId, buyerId);
        return toResponse(requireAuction(auctionId));
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    // API đơn lẻ (getById, create, placeBid...)
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