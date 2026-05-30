package models;

import models.Exceptions.AuthenticationException;
import models.Exceptions.CustomisedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface Seller {
    boolean isBlocked();

    default Auction createAuction(Item item, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        if (this.isBlocked()) {
            throw new AuthenticationException("Your account is currently blocked and cannot create auctions.");
        }

        if (terminatedAt == null || createdAt == null) {
            throw new CustomisedException("Scheduled auctions must have both starting and ending time");
        }

        if (!terminatedAt.isAfter(createdAt)) {
            throw new CustomisedException("Ending time must be after starting time.");
        }

        if (terminatedAt.isBefore(LocalDateTime.now())) {
            throw new CustomisedException("The auction termination time must be in the future.");
        }

        long durationDays = ChronoUnit.DAYS.between(createdAt, terminatedAt);
        if (durationDays > 30) {
            throw new CustomisedException("Auction duration cannot exceed 30 days!");
        }

        try {
            if (this instanceof User owner) {
                Auction auction = AuctionManager.getInstance().createAuction(owner, item, createdAt, terminatedAt);
                System.out.println("[System]: Auction created successfully for item: " + item.getName());
                return auction;
            } else {
                throw new CustomisedException("Invalid seller type.");
            }
        } catch (Exception e) {
            throw new CustomisedException("System error: " + e.getMessage());
        }
    }
}