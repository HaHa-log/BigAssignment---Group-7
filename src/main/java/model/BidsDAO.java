package model;

import Branch.Bid;
import Branch.User;

import java.util.List;

public interface BidsDAO extends DAO<Bid> {

    List<Bid> getByAuctionId(int id);

}
