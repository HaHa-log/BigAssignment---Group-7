package Branch.Common;

public record AuctionHistoryEntry(
        int auctionId,
        String itemName,
        String auctionStatus,
        String userState
) {}