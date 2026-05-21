package com.group7.dto.auction;

public class ConfirmReceiptRequest {
    private int buyerId;

    public ConfirmReceiptRequest() {}

    public ConfirmReceiptRequest(int buyerId) {
        this.buyerId = buyerId;
    }
    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }
}
