package controllers;

import com.group7.dto.bid.AutoBidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AutoBidService;

@RestController
@RequestMapping("/api/autobids")
public class AutoBidController {
    private final AutoBidService autoBidService;

    public AutoBidController(AutoBidService autoBidService) {
        this.autoBidService = autoBidService;
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<?> enableAutoBid(@PathVariable int auctionId, @RequestBody AutoBidRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(autoBidService.createOrUpdate(auctionId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<?> getByAuctionId(@PathVariable int auctionId) {
        try {
            return ResponseEntity.ok(autoBidService.getByAuctionId(auctionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}