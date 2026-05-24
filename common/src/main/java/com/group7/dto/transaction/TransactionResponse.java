package com.group7.dto.transaction;

import java.time.LocalDateTime;

public record TransactionResponse(
        int transactionId,
        int auctionId,
        String itemName,
        int buyerId,
        String buyerName,
        int sellerId,
        String sellerName,
        double finalAmount,
        String status,
        LocalDateTime paidAt,
        LocalDateTime completedAt,
        LocalDateTime expiryTime
) {}