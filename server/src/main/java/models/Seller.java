package models;

import models.Exceptions.AuthenticationException;
import models.Exceptions.CustomisedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Seller {
    Logger log = LoggerFactory.getLogger(Seller.class);

    boolean isBlocked();

    default Auction createAuction(Item item, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        if (this.isBlocked()) {
            log.error("Auction creation blocked: Seller account status is currently restricted.");
            throw new AuthenticationException("Your account is currently blocked and cannot create auctions.");
        }

        if (terminatedAt == null || createdAt == null) {
            log.warn("Auction creation rejected: Missing timestamp specifications.");
            throw new CustomisedException("Scheduled auctions must have both starting and ending time");
        }

        if (!terminatedAt.isAfter(createdAt)) {
            log.warn("Auction creation rejected: Invalid timeline order. Start: {}, End: {}", createdAt, terminatedAt);
            throw new CustomisedException("Ending time must be after starting time.");
        }

        if (terminatedAt.isBefore(LocalDateTime.now())) {
            log.warn("Auction creation rejected: Termination time occurs in the past. Expiration set: {}", terminatedAt);
            throw new CustomisedException("The auction termination time must be in the future.");
        }

        long durationDays = ChronoUnit.DAYS.between(createdAt, terminatedAt);
        if (durationDays > 30) {
            log.warn("Auction creation rejected: Timeline duration constraints breached. Days: {}", durationDays);
            throw new CustomisedException("Auction duration cannot exceed 30 days!");
        }

        try {
            if (this instanceof User owner) {
                Auction auction = AuctionManager.getInstance().createAuction(owner, item, createdAt, terminatedAt);
                log.info("Auction successfully deployed. Item: '{}' | Created by: '{}'", item.getName(), owner.getFullName());
                return auction;
            } else {
                log.error("Auction creation halted: Invalid class implementation for interface Seller.");
                throw new CustomisedException("Invalid seller type.");
            }
        } catch (Exception e) {
            log.error("System pipeline breakdown during auction allocation: {}", e.getMessage(), e);
            throw new CustomisedException("System error: " + e.getMessage());
        }
    }
}