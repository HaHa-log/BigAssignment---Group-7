package repositories;

import models.Bid;

import java.util.List;

public interface BidsDAO extends DAO<Bid> {

    List<Bid> getByAuctionId(int id);

}
