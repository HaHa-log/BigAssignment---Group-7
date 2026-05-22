package controllers;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ItemResponse>> getInventoryByOwner(@PathVariable int ownerId) {
        List<ItemResponse> responses = itemService.getItemsByOwner(ownerId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemRequest request) {
        try {
            itemService.createNewItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Logs unexpected issues (like DB exceptions) to BE console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }
}