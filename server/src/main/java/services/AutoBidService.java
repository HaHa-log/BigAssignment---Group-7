package services;

import com.group7.dto.bid.AutoBidRequest;
import com.group7.dto.bid.AutoBidResponse;
import models.Auction;
import models.AutoBid;
import models.Member;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class AutoBidService {
    private final AutoBidDAO autoBidDAO = DaoFactory.createAutoBidDAO();
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();

    public AutoBidResponse createOrUpdate(int auctionId, AutoBidRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Error]: Request body is required.");
        }

        Auction auction = requireAuction(auctionId);
        Member member = usersDAO.getById(request.getBidderId());
        if (member == null) {
            throw new IllegalArgumentException("[Error]: Bidder is invalid.");
        }

        AutoBid autoBid = new AutoBid(auction, member, request.getMaxBid(), request.getIncrement());
        autoBidDAO.save(autoBid);

        models.AuctionManager.getInstance().processAutoBids(auction, autoBid);

        return toResponse(autoBid);
    }

    public List<AutoBidResponse> getByAuctionId(int auctionId) {
        return autoBidDAO.getByAuctionId(auctionId).stream()
                .map(this::toResponse)
                .toList();
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

        return new AutoBidResponse(
                auctionId,
                bidderId,
                bidderName,
                autoBid.getMaxBid(),
                autoBid.getIncrement()
        );
    }
}