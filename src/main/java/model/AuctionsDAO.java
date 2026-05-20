package model;

import Branch.Auction;
import Branch.Item;

import java.util.List;

public interface AuctionsDAO extends DAO<Auction> {

    List<Auction> getActiveAuctions();

    Auction getByItem (Item item);
}
