package controllers;

import dto.auction.BidRequest;
import dto.auction.ConfirmReceiptRequest;
import dto.auction.CreateAuctionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.AuctionService;

import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(auctionService.getAll());
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(auctionService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAuctionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.create(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<?> placeBid(@PathVariable int id, @RequestBody BidRequest request) {
        try {
            return ResponseEntity.ok(auctionService.placeBid(id, request.getBidderId(), request.getAmount()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable int id) {
        try {
            return ResponseEntity.ok(auctionService.cancel(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PostMapping("/{id}/confirm-receipt")
    public ResponseEntity<?> confirmReceipt(@PathVariable int id, @RequestBody ConfirmReceiptRequest request) {
        try {
            return ResponseEntity.ok(auctionService.confirmReceipt(id, request.getBuyerId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    private ResponseEntity<?> serverError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() == null ? "Unexpected server error." : e.getMessage()));
    }
}
