package controllers;

import com.group7.dto.auction.*;
import com.group7.dto.bid.BidRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuctionService;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ALL") String status) {

        return ResponseEntity.ok(auctionService.getAll(page, size, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(auctionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> create(@RequestBody CreateAuctionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.create(request));
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<AuctionResponse> placeBid(@PathVariable int id, @RequestBody BidRequest request) {
        return ResponseEntity.ok(auctionService.placeBid(id, request.getBidderId(), request.getAmount()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AuctionResponse> cancel(@PathVariable int id) {
        return ResponseEntity.ok(auctionService.cancel(id));
    }

    @PostMapping("/{id}/confirm-receipt")
    public ResponseEntity<AuctionResponse> confirmReceipt(
            @PathVariable int id,
            @RequestBody ConfirmReceiptRequest request) {
        return ResponseEntity.ok(auctionService.confirmReceipt(id, request.getBuyerId()));
    }
}
