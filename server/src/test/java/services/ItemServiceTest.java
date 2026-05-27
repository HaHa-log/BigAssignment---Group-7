package services;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import com.group7.dto.item.UpdateItemRequest;
import models.Item;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.ItemsDAO;
import repositories.UsersDAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock private ItemsDAO itemsDAO;
    @Mock private UsersDAO usersDAO;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemsDAO, usersDAO);
    }

    @Test
    @DisplayName("Create new item successfully")
    void createNewItem_Success() {
        ItemRequest req = new ItemRequest();
        req.setName("Laptop Gaming");
        req.setStartingPrice(1500.0);
        req.setOwnerId(10);
        req.setDescription("New 99%");

        User mockOwner = mock(User.class);
        when(usersDAO.getById(10)).thenReturn(mockOwner);

        Item createdItem = itemService.createNewItem(req);

        assertNotNull(createdItem);
        assertEquals("Laptop Gaming", createdItem.getName());
        assertEquals(Item.Status.AVAILABLE, createdItem.getStatus());
    }

    @Test
    @DisplayName("Update item details successfully")
    void updateItem_Success() {
        int itemId = 1;
        UpdateItemRequest req = new UpdateItemRequest();
        req.setName("Laptop Gaming Pro");
        req.setStartingPrice(1600.0);

        Item mockItem = mock(Item.class);
        User mockUser = mock(User.class);

        when(mockItem.getId()).thenReturn(itemId);
        when(mockItem.getName()).thenReturn("Laptop Gaming Pro");
        when(mockItem.getStartingPrice()).thenReturn(1600.0);
        when(mockItem.getDescription()).thenReturn("New 99%");
        when(mockItem.getOwner()).thenReturn(mockUser);
        when(mockItem.getOwnerId()).thenReturn(10);
        when(mockUser.getFullName()).thenReturn("Owner Name");
        when(mockItem.getStatus()).thenReturn(Item.Status.AVAILABLE);

        when(itemsDAO.getById(itemId)).thenReturn(mockItem);

        ItemResponse resp = itemService.update(itemId, req);

        assertNotNull(resp);
        verify(itemsDAO, times(1)).update(mockItem);
    }
}