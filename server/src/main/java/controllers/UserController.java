package controllers;

import com.group7.dto.user.HistoryEntryResponse;
import com.group7.dto.user.NotificationResponse;
import com.group7.dto.user.ChangePasswordRequest;
import com.group7.dto.user.UserResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.FileStorageService;
import services.UserService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/email/{email:.+}")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> uploadAvatar(@PathVariable int id, @RequestPart("file") MultipartFile file) {
        String filename = fileStorageService.saveAvatar(file, id);
        return ResponseEntity.ok(userService.updateAvatar(id, filename));
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<UserResponse> changePassword(
            @PathVariable int id,
            @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(userService.changePassword(id, request));
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
    public ResponseEntity<UserResponse> block(@PathVariable int id) {
            return ResponseEntity.ok(userService.block(id));
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<UserResponse> unblock(@PathVariable int id) {
        return ResponseEntity.ok(userService.unblock(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<UserResponse> deposit(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.deposit(id, body.get("amount")));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<UserResponse> withdraw(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.withdraw(id, body.get("amount")));
    }

    @PostMapping("/{id}/freeze")
    public ResponseEntity<UserResponse> freeze(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.freeze(id, body.get("amount")));
    }

    @PostMapping("/{id}/unfreeze")
    public ResponseEntity<UserResponse> unfreeze(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.unfreeze(id, body.get("amount")));
    }

    @PostMapping("/{id}/spend-frozen")
    public ResponseEntity<UserResponse> spendFrozen(@PathVariable int id, @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(userService.spendFrozen(id, body.get("amount")));
    }

    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable int id) {
        return ResponseEntity.ok(userService.getNotifications(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HistoryEntryResponse>> getAuctionHistory(@PathVariable int id) {
        return ResponseEntity.ok(userService.getAuctionHistory(id));
    }
}
