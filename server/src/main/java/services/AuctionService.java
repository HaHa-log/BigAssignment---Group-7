package services;

import com.group7.dto.auction.*;
import models.Auction;
import models.AuctionManager;
import models.Bidder;
import models.Item;
import models.Member;
import models.Transaction;
import models.User;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class AuctionService {
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();

    public List<AuctionResponse> getAll() {
        return auctionsDAO.getAll().stream()
                .map(this::toResponse)
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

        User owner = usersDAO.getById(request.getOwnerId());
        if (!(owner instanceof Member member)) {
            throw new IllegalArgumentException("[Error]: Auction owner is invalid.");
        }

        Item item = new Item(request.getItemName(), request.getStartingPrice(), request.getDescription());
        item.setOwner(member);
        item.setImagePath(request.getImagePath());

        Auction auction = new Auction(member, item, request.getStartingTime(), request.getEndingTime());
        auctionsDAO.save(auction);
        auction.start();
        auctionsDAO.update(auction);
        return toResponse(auction);
    }

    public AuctionResponse placeBid(int auctionId, int bidderId, double amount) {
        Auction auction = requireAuction(auctionId);
        User bidder = usersDAO.getById(bidderId);
        if (!(bidder instanceof Bidder)) {
            throw new IllegalArgumentException("[Error]: Bidder is invalid.");
        }

        bidder.placeBid(auction, amount);
        return toResponse(auctionsDAO.getById(auctionId));
    }

    public AuctionResponse cancel(int auctionId) {
        Auction auction = requireAuction(auctionId);
        auction.transitionTo(Auction.AuctionStatus.CANCELED);
        auctionsDAO.update(auction);
        return toResponse(auction);
    }

    public AuctionResponse confirmReceipt(int auctionId, int buyerId) {
        User buyer = usersDAO.getById(buyerId);
        if (!(buyer instanceof Member member)) {
            throw new IllegalArgumentException("[Error]: Buyer is invalid.");
        }

        Transaction transaction = AuctionManager.getInstance().confirmReceipt(auctionId, member);
        return toResponse(transaction.getAuction());
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    private AuctionResponse toResponse(Auction auction) {
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
                winner != null ? winner.getId() : null,         // 13. winnerId
                winner != null ? winner.getFullName() : null    // 14. winnerName
        );
    }
}
