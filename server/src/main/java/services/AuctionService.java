package services;

import dto.auction.AuctionResponse;
import models.Auction;
import models.Item;
import models.User;
import repositories.AuctionsDAO;
import repositories.impl.DaoFactory;

import java.util.ArrayList;
import java.util.List;

public class AuctionService {

    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();

    public List<AuctionResponse> getAllAuctions() {
        List<Auction> auctions = auctionsDAO.getAll();
        List<AuctionResponse> result = new ArrayList<>();

        for (Auction auction : auctions) {
            result.add(toResponse(auction));
        }

        return result;
    }

    public List<AuctionResponse> getByStatus(String status) {
        List<Auction> auctions = auctionsDAO.getByStatus(status);
        List<AuctionResponse> result = new ArrayList<>();

        for (Auction auction : auctions) {
            result.add(toResponse(auction));
        }

        return result;
    }

    public List<AuctionResponse> getActiveAuctions() {
        List<Auction> auctions = auctionsDAO.getActiveAuctions();
        List<AuctionResponse> result = new ArrayList<>();

        for (Auction auction : auctions) {
            result.add(toResponse(auction));
        }

        return result;
    }

    public AuctionResponse getAuctionById(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("Auction not found: " + id);
        }
        return toResponse(auction);
    }

    private AuctionResponse toResponse(Auction auction) {
        Item item = auction.getItem();
        User owner = auction.getOwner();
        User winner = auction.getWinner();

        Integer itemId = item.getId();
        String itemName = item.getName();

        Integer ownerId = owner.getId();
        String ownerName = owner.getFullName() ;

        Integer winnerId = winner != null ? winner.getId() : null;
        String winnerName = winner != null ? winner.getFullName() : null;

        String status = auction.getRawStatus().name();

        return new AuctionResponse(auction.getId(), itemId, itemName, ownerId, ownerName, winnerId, winnerName, auction.getCurrentPrice(), status);
    }
}
