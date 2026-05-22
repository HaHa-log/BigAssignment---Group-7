package controllers;

import com.group7.dto.bid.BidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.BidService;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(bidService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(bidService.getById(id));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<?> getByAuctionId(@PathVariable int auctionId) {
        return ResponseEntity.ok(bidService.getByAuctionId(auctionId));
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<?> create(@PathVariable int auctionId, @RequestBody BidRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.create(auctionId, request));
    }
}