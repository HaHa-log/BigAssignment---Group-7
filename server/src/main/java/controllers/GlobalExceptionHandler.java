package controllers;

import config.DbException;
import models.Exceptions.AuctionClosedException;
import models.Exceptions.AuthenticationException;
import models.Exceptions.InvalidBidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice; // Updated import
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@RestControllerAdvice // Fixed annotation
public class GlobalExceptionHandler {

    @ExceptionHandler(AuctionClosedException.class)
    public ResponseEntity<Map<String, String>> handleAuctionClosed(AuctionClosedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "exceptionType", "AuctionClosedException",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBid(InvalidBidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "exceptionType", "InvalidBidException",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exceptionType", "AuthenticationException",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(DbException.class)
    public ResponseEntity<Map<String, String>> handleDb(DbException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exceptionType", "DbException",
                "message", "Database error occurred."
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "exceptionType", "IllegalArgumentException",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnknown(Exception e) {
        e.printStackTrace(); // Keep this for server-side logging!
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exceptionType", "GenericException",
                "message", "Unexpected server error."
        ));
    }
}