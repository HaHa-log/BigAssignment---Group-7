package controllers;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import com.group7.dto.item.UpdateItemRequest;
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

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getById(@PathVariable int id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ItemResponse>> getInventoryByOwner(@PathVariable int ownerId) {
        List<ItemResponse> responses = itemService.getItemsByOwner(ownerId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemRequest request) {
        itemService.createNewItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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