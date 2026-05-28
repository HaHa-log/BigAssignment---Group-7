package controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.FileStorageService;
import services.UserService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/email/{email:.+}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getByEmail(email));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@PathVariable int id, @RequestPart("file") MultipartFile file) {
        String filename = fileStorageService.saveAvatar(file, id);
        return ResponseEntity.ok(userService.updateAvatar(id, filename));
    }

    @GetMapping("/avatars/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) throws IOException {
        Path path = fileStorageService.resolveAvatar(filename);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String lower = filename.toLowerCase();
        String contentType = lower.endsWith(".png") ? "image/png"
                : lower.endsWith(".webp") ? "image/webp"
                : "image/jpeg";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
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

    @GetMapping("/{id}/notifications")
    public ResponseEntity<?> getNotifications(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getNotifications(id));
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getAuctionHistory(@PathVariable int id) {
        try {
            return ResponseEntity.ok(userService.getAuctionHistory(id));
        } catch (Exception e) {
            return serverError(e);
        }
    }
}
