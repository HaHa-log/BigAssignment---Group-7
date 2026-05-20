package repositories;

import models.Auction;

import java.util.List;

public interface AuctionsDAO extends DAO<Auction> {

    List<Auction> getActiveAuctions();
}
