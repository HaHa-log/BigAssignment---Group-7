package services;

import com.group7.dto.bid.AutoBidRequest;
import com.group7.dto.bid.AutoBidResponse;
import models.Auction;
import models.AuctionManager;
import models.AutoBid;
import models.User;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class AutoBidService {
    private final AutoBidDAO autoBidDAO;
    private final AuctionsDAO auctionsDAO;
    private final UsersDAO usersDAO;
    private final AuctionManager auctionManager;

    public AutoBidService() {
        this.autoBidDAO = DaoFactory.createAutoBidDAO();
        this.auctionsDAO = DaoFactory.createAuctionsDAO();
        this.usersDAO = DaoFactory.createUsersDAO();
        this.auctionManager = AuctionManager.getInstance();
    }

    public AutoBidService(AutoBidDAO autoBidDAO, AuctionsDAO auctionsDAO, UsersDAO usersDAO, AuctionManager auctionManager) {
        this.autoBidDAO = autoBidDAO;
        this.auctionsDAO = auctionsDAO;
        this.usersDAO = usersDAO;
        this.auctionManager = auctionManager;
    }

    public AutoBidResponse createOrUpdate(int auctionId, AutoBidRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Error]: Request body is required.");
        }

        Auction auction = requireAuction(auctionId);
        User user = usersDAO.getById(request.getBidderId());
        if (user == null) {
            throw new IllegalArgumentException("[Error]: Bidder is invalid.");
        }

        AutoBid autoBid = new AutoBid(auction, user, request.getMaxBid(), request.getIncrement());
        autoBidDAO.save(autoBid);

        this.auctionManager.processAutoBids(auction, autoBid);

        return toResponse(autoBid);
    }

    public List<AutoBidResponse> getByAuctionId(int auctionId) {
        return autoBidDAO.getByAuctionId(auctionId).stream().map(this::toResponse).toList();
    }

    private Auction requireAuction(int id) {
        Auction auction = auctionsDAO.getById(id);
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction not found.");
        }
        return auction;
    }

    private AutoBidResponse toResponse(AutoBid autoBid) {
        int auctionId = autoBid.getAuction() != null ? autoBid.getAuction().getId() : 0;
        int bidderId = autoBid.getUser() != null ? autoBid.getUser().getId() : 0;
        String bidderName = autoBid.getUser() != null ? autoBid.getUser().getFullName() : null;
        return new AutoBidResponse(auctionId, bidderId, bidderName, autoBid.getMaxBid(), autoBid.getIncrement());
    }
}