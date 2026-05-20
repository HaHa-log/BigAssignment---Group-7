package models;

public abstract class Entity {
    private int id;
    //Things like Auction and Transaction will not have a name

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    //Thinking of setting conditions for setName()
}
