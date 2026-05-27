package services;

import models.Auction;
import models.Item;
import models.Bid;
import models.User;
import com.group7.dto.auction.*;
import com.group7.dto.bid.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AuctionMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private AuctionMapper() {
    }

    public static List<Auction> toAuctionList(List<AuctionResponse> responses) {
        if (responses == null) {
            return new ArrayList<>();
        }

        // Identity Map: Tái sử dụng vùng nhớ User xuyên suốt danh sách dữ liệu nhận về
        Map<Integer, User> userCache = new HashMap<>();
        List<Auction> auctions = new ArrayList<>(responses.size());

        for (AuctionResponse response : responses) {
            auctions.add(toAuctionInternal(response, userCache));
        }
        return auctions;
    }

    public static Auction toAuction(AuctionResponse response) {
        if (response == null) return null;
        return toAuctionInternal(response, new HashMap<>());
    }

    // Hàm ánh xạ nội bộ xử lý tốc độ cao kèm Cache trạng thái
    private static Auction toAuctionInternal(AuctionResponse response, Map<Integer, User> userCache) {
        User owner = getOrCreateUser(response.getOwnerId(), response.getOwnerName(), userCache);

        Item item = new Item(
                response.getItemName(),
                response.getStartingPrice(),
                response.getItemDescription(),
                Item.Status.IN_AUCTION,
                response.getItemImagePath()
        );
        item.setId(response.getItemId());
        item.setOwnerId(response.getOwnerId());

        User winner = null;
        if (response.getWinnerId() != null && response.getWinnerId() > 0) {
            winner = getOrCreateUser(response.getWinnerId(), response.getWinnerName(), userCache);
        }

        Auction auction = new Auction(
                owner,
                item,
                parseAuctionStatus(response.getStatus()),
                response.getStartingTime(),
                response.getEndingTime(),
                response.getStartingPrice(),
                response.getCurrentPrice(),
                winner
        );
        auction.setAuctionId(response.getId());

        if (response.getBids() != null && !response.getBids().isEmpty()) {
            List<Bid> domainBids = new ArrayList<>(response.getBids().size());
            for (BidResponse bidDto : response.getBids()) {
                User bidder = null;
                if (bidDto.getBidderId() > 0) {
                    bidder = getOrCreateUser(bidDto.getBidderId(), bidDto.getBidderName(), userCache);
                }

                domainBids.add(new Bid(
                        auction,
                        bidder,
                        bidDto.getBidPrice(),
                        bidDto.getBidTime()
                ));
            }
            auction.setBids(domainBids);
        } else {
            auction.setBids(new ArrayList<>());
        }

        return auction;
    }

    // Kiểm tra cache trước khi cấp phát vùng nhớ mới
    private static User getOrCreateUser(int id, String fullName, Map<Integer, User> userCache) {
        return userCache.computeIfAbsent(id, userId -> {
            String[] nameParts = splitNameOptimized(fullName);
            User user = new User(
                    nameParts[0],
                    nameParts[1],
                    "user-" + userId + "@server.local",
                    "0000000000",
                    SESSION_PASSWORD_PLACEHOLDER,
                    0,
                    false,
                    false,
                    null,
                    null
            );
            user.setId(userId);
            return user;
        });
    }

    private static String[] splitNameOptimized(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return new String[] {"Unknown", "User"};
        }

        String trimmed = fullName.trim();
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace == -1) {
            return new String[] {trimmed, "User"};
        }

        return new String[] {
                trimmed.substring(0, firstSpace),
                trimmed.substring(firstSpace).trim()
        };
    }

    private static Auction.AuctionStatus parseAuctionStatus(String statusStr) {
        if (statusStr == null) return Auction.AuctionStatus.OPEN;
        try {
            return Auction.AuctionStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Auction.AuctionStatus.OPEN;
        }
    }
}