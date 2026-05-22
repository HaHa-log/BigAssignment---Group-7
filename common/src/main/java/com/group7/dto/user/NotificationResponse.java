package com.group7.dto.user;

public record NotificationResponse(
        String type,
        String itemName,
        double currentPrice
) {}
