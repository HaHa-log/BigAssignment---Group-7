package dto.auction;

public class AuctionResponse {
    private int id;

    private Integer itemId;
    private String itemName;

    private Integer ownerId;
    private String ownerName;

    private Integer winnerId;
    private String winnerName;

    private double currentPrice;
    private String status;

    public AuctionResponse() {
    }

    public AuctionResponse(int id, Integer itemId, String itemName, Integer ownerId, String ownerName, Integer winnerId, String winnerName, double currentPrice, String status) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.winnerId = winnerId;
        this.winnerName = winnerName;
        this.currentPrice = currentPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getStatus() {
        return status;
    }
}