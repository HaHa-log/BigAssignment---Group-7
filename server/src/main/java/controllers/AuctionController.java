package controllers;

import com.group7.dto.auction.*;
import com.group7.dto.bid.BidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        java.util.List<AuctionResponse> auctions = auctionService.getAll(page, size);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(auctionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAuctionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.create(request));
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<?> placeBid(@PathVariable int id, @RequestBody BidRequest request) {
        return ResponseEntity.ok(auctionService.placeBid(id, request.getBidderId(), request.getAmount()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable int id) {
        return ResponseEntity.ok(auctionService.cancel(id));
    }

    @PostMapping("/{id}/confirm-receipt")
    public ResponseEntity<?> confirmReceipt(@PathVariable int id, @RequestBody ConfirmReceiptRequest request) {
        return ResponseEntity.ok(auctionService.confirmReceipt(id, request.getBuyerId()));
    }

    private ResponseEntity<?> serverError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() == null ? "Unexpected server error." : e.getMessage()));
    }
}