package models.dto.auction;

public class ConfirmReceiptRequest {
    private int buyerId;

    public ConfirmReceiptRequest(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getBuyerId() {
        return buyerId;
    }
}
