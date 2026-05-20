package repositories;

import models.AutoBid;

import java.util.List;

public interface AutoBidDAO extends DAO<AutoBid> {
    List<AutoBid> getByAuctionId(int auctionId);

    AutoBid getByAuctionAndUser(int auctionId, int userId);
}