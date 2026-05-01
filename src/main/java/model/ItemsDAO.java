package model;

import Branch.Item;

import java.util.List;

public interface ItemsDAO extends DAO<Item> {

    List<Item> getByName(String name);

}
