package controllers;

import config.DbException;
import models.Exceptions.AuctionClosedException;
import models.Exceptions.AuthenticationException;
import models.Exceptions.InvalidBidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuctionClosedException.class)
    public ResponseEntity<Map<String, String>> handleAuctionClosed(AuctionClosedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "exceptionType", "AuctionClosedException",
                "error", e.getMessage() != null ? e.getMessage() : "",
                "message", e.getMessage() != null ? e.getMessage() : ""
        ));
    }

    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBid(InvalidBidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "exceptionType", "InvalidBidException",
                "error", e.getMessage() != null ? e.getMessage() : "",
                "message", e.getMessage() != null ? e.getMessage() : ""
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exceptionType", "AuthenticationException",
                "error", e.getMessage() != null ? e.getMessage() : "",
                "message", e.getMessage() != null ? e.getMessage() : ""
        ));
    }

    @ExceptionHandler(DbException.class)
    public ResponseEntity<Map<String, String>> handleDb(DbException e) {
        // 1. Dòng này cực kỳ quan trọng: Ép Server phải in lỗi chi tiết ra tab Console của Spring Boot
        e.printStackTrace();

        // 2. Lấy thông báo lỗi thật từ Database ném ra
        String realMessage = e.getMessage() != null ? e.getMessage() : "Database error occurred.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exceptionType", "DbException",
                "error", realMessage, // Trả lỗi thật về để xem
                "message", realMessage
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "exceptionType", "IllegalArgumentException",
                "error", e.getMessage() != null ? e.getMessage() : "",
                "message", e.getMessage() != null ? e.getMessage() : ""
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnknown(Exception e) {
        e.printStackTrace();
        String msg = e.getMessage() != null ? e.getMessage() : "Unexpected server error.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exceptionType", "GenericException",
                "error", msg,
                "message", msg
        ));
    }
}