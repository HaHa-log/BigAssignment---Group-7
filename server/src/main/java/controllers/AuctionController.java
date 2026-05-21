package controllers;

import dto.auction.BidRequest;
import dto.auction.ConfirmReceiptRequest;
import dto.auction.CreateAuctionRequest;
import models.Exceptions.AuctionClosedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.AuctionService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return ResponseEntity.ok(auctionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(auctionService.getById(id));
    }

    @GetMapping("/api/items/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get("src/main/resources/ItemImages/").resolve(filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
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