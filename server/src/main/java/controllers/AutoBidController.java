package controllers;

import com.group7.dto.bid.AutoBidRequest;
import com.group7.dto.bid.AutoBidResponse;
import java.util.List;
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
    public ResponseEntity<AutoBidResponse> enableAutoBid(
            @PathVariable int auctionId,
            @RequestBody AutoBidRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(autoBidService.createOrUpdate(auctionId, request));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<AutoBidResponse>> getByAuctionId(@PathVariable int auctionId) {
        return ResponseEntity.ok(autoBidService.getByAuctionId(auctionId));
    }
}
