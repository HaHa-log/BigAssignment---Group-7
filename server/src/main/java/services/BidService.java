package services;

import com.group7.dto.bid.*;
import models.Auction;
import models.Bid;
import models.Common.Price;
import models.User;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.BidsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class BidService {
    private final BidsDAO bidsDAO = DaoFactory.createBidsDAO();
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();

    public List<BidResponse> getAll() {
        return bidsDAO.getAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BidResponse getById(int id) {
        Bid bid = requireBid(id);
        return toResponse(bid);
    }

    public List<BidResponse> getByAuctionId(int auctionId) {
        return bidsDAO.getByAuctionId(auctionId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BidResponse create(int auctionId, BidRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Error]: Request body is required.");
        }

        Auction auction = requireAuction(auctionId);
        User user = usersDAO.getById(request.getBidderId());
        if (user == null) {
            throw new IllegalArgumentException("[Error]: Bidder is invalid.");
        }

        Bid bid = new Bid(auction, user, new Price(request.getAmount()));

        bidsDAO.save(bid);
        return toResponse(bid);
    }

    private Bid requireBid(int id) {
        Bid bid = bidsDAO.getById(id);
        if (bid == null) {
            throw new IllegalArgumentException("[Error]: Bid not found.");
        }
        return bid;
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    private BidResponse toResponse(Bid bid) {
        int auctionId = bid.getAuction().getId();
        User bidder = bid.getBidder();
        int bidderId = bidder != null ? bidder.getId() : 0;
        String bidderName = bidder != null ? bidder.getFullName() : null;

        return new BidResponse(
                bid.getId(),
                auctionId,
                bidderId,
                bidderName,
                bid.getBidPrice().getPrice(),
                bid.getBidTime()
        );
    }
}
