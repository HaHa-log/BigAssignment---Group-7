package controllers;

import dto.auction.AuctionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuctionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService = new AuctionService();

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<AuctionResponse> auctions = auctionService.getAllAuctions();
            return ResponseEntity.ok(auctions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cannot fetch auctions.", "detail", safeMessage(e)));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            AuctionResponse auction = auctionService.getAuctionById(id);
            return ResponseEntity.ok(auction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cannot fetch auction.", "detail", safeMessage(e)));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String status) {
        try {
            List<AuctionResponse> auctions = auctionService.getByStatus(status);
            return ResponseEntity.ok(auctions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cannot fetch auctions.", "detail", safeMessage(e)));
        }
    }

    @GetMapping
    public ResponseEntity<?> getActiveAuction() {
        try {
             List<AuctionResponse> auctions = auctionService.getActiveAuctions();            return ResponseEntity.ok(auctions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Cannot fetch auctions.", "detail", safeMessage(e)));
        }
    }

    private String safeMessage(Exception e) {
        return e.getMessage() == null ? "no detail" : e.getMessage();
    }
}
