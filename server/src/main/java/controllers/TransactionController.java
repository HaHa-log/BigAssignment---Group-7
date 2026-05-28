package controllers;

import com.group7.dto.transaction.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.TransactionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getByUser(@PathVariable int userId) {
        List<TransactionResponse> list = transactionService.getByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @PostMapping("/auction/{auctionId}/create-pending")
    public ResponseEntity<TransactionResponse> createPending(@PathVariable int auctionId) {
        TransactionResponse response = transactionService.createPendingTransaction(auctionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auction/{auctionId}/confirm-receipt")
    public ResponseEntity<TransactionResponse> confirmReceipt(
            @PathVariable int auctionId,
            @RequestBody Map<String, Integer> body
    ) {
        int buyerId = body.get("buyerId");
        return ResponseEntity.ok(transactionService.confirmReceipt(auctionId, buyerId));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<TransactionResponse> refund(@PathVariable int id) {
        return ResponseEntity.ok(transactionService.refundTransaction(id));
    }
}
