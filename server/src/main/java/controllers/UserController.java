package controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable int id) {
            return ResponseEntity.ok(userService.block(id));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable int id) {
        return ResponseEntity.ok(userService.unblock(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.deposit(id, body.get("amount")));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.withdraw(id, body.get("amount")));
    }

    @PostMapping("/{id}/freeze")
    public ResponseEntity<?> freeze(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.freeze(id, body.get("amount")));
    }

    @PostMapping("/{id}/unfreeze")
    public ResponseEntity<?> unfreeze(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.unfreeze(id, body.get("amount")));
    }

    @PostMapping("/{id}/spend-frozen")
    public ResponseEntity<?> spendFrozen(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.spendFrozen(id, body.get("amount")));
    }

    private ResponseEntity<?> serverError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() == null ? "Unexpected server error." : e.getMessage()));
    }
}
