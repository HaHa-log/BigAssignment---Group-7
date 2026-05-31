package repositories;

import models.Auction;
import models.AutoBid;

import java.util.List;

public interface AutoBidDAO extends DAO<AutoBid> {
    List<AutoBid> getByAuctionId(int auctionId, Auction auction);

    AutoBid getByAuctionAndUser(int auctionId, int userId);
}