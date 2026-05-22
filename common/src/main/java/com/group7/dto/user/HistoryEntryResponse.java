package com.group7.dto.user;

public record HistoryEntryResponse(
        int auctionId,
        String itemName,
        String auctionStatus,
        String userState
) {}
