package controllers;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import com.group7.dto.item.UpdateItemRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.FileStorageService;
import services.ItemService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final FileStorageService fileStorageService;

    public ItemController(ItemService itemService, FileStorageService fileStorageService) {
        this.itemService = itemService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ItemResponse>> getInventoryByOwner(
            @PathVariable int ownerId,
            @RequestParam(defaultValue = "0") int page,       // Thêm tham số page
            @RequestParam(defaultValue = "10") int size) {     // Thêm tham số size
        List<ItemResponse> responses = itemService.getItemsByOwner(ownerId, page, size);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody ItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createNewItem(request));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> uploadItemImage(@PathVariable int id, @RequestPart("file") MultipartFile file) {
        String filename = fileStorageService.saveItemImage(file, id);
        return ResponseEntity.ok(itemService.updateImage(id, filename));
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getItemImage(@PathVariable String filename) throws IOException {
        Path path = fileStorageService.resolveItem(filename);
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

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(@PathVariable int id, @RequestBody UpdateItemRequest request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
