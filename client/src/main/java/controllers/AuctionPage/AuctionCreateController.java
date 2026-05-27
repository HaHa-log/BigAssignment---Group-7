package controllers.AuctionPage;

import com.group7.dto.item.ItemRequest;
import javafx.scene.control.ComboBox;
import models.Auction;
import models.Item;
import models.SessionManager;
import com.group7.dto.auction.CreateAuctionRequest;
import models.User;
import services.AuctionApiService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ItemApiService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class AuctionCreateController {
    @FXML private ComboBox<Item> itemDropdown; // NEW: The user selects their existing item

    @FXML private TextField itemNameInput, descriptionInput, startingPriceInput;
    @FXML private DatePicker startingDateInput, endingDateInput;
    @FXML private TextField startingTimeInput, endingTimeInput;
    @FXML private Label fileNameLabel, auctionCreateResult, itemCreateResult;
    @FXML private ImageView imagePreview;

    private final AuctionApiService auctionApiService = new AuctionApiService();
    private final ItemApiService itemApiService = new ItemApiService();

    private File selectedImageFile;

    @FXML
    public void initialize() {
        itemDropdown.setConverter(new javafx.util.StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                return (item == null) ? "" : item.getName(); // Displays the item name
            }

            @Override
            public Item fromString(String string) {
                return null; // Not needed for a non-editable ComboBox
            }
        });

        loadAvailableItems();
    }

    @FXML
    private void createItem() {
        String itemName = itemNameInput.getText();
        String description = descriptionInput.getText();
        String startingPriceRaw = startingPriceInput.getText();

        if (itemName.trim().isEmpty() || startingPriceRaw.trim().isEmpty()) {
            itemCreateResult.setTextFill(RED);
            itemCreateResult.setText("[Error]: Please fill all required fields");
            return;
        }

        Task<Void> createTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (!(SessionManager.getCurrentUser() instanceof User seller)) {
                    throw new IllegalArgumentException("[Error]: Session expired! Please log in again.");
                }

                double startingPrice = Double.parseDouble(startingPriceRaw);

                Item item = itemApiService.create(new ItemRequest(
                        itemName, startingPrice,
                        description, seller.getId()
                ));

                if (selectedImageFile == null) {
                    return null;
                } else {
                    itemApiService.uploadItemImage(item.getId(), selectedImageFile);
                }

                return null;
            }
        };

        createTask.setOnSucceeded(e -> {
            itemCreateResult.setTextFill(GREEN);
            itemCreateResult.setText("Item created successfully!");
            clearInputs();
        });

        createTask.setOnFailed(e -> {
            Throwable ex = createTask.getException();
            auctionCreateResult.setTextFill(RED);
            if (ex instanceof IllegalArgumentException) {
                itemCreateResult.setText(ex.getMessage());
            } else if (ex instanceof IOException) {
                itemCreateResult.setText("File Error: Could not save image.");
            } else {
                itemCreateResult.setText("Unexpected error: " + ex.getMessage());
            }
        });

        new Thread(createTask).start();
    }

    @FXML
    private void createAuction() {
        Item selectedItem = itemDropdown.getValue();

        LocalDate startDate = startingDateInput.getValue();
        LocalDate endDate = endingDateInput.getValue();
        String sTimeStr = startingTimeInput.getText();
        String eTimeStr = endingTimeInput.getText();

        // Basic validation
        if (startDate == null || endDate == null) {
            auctionCreateResult.setTextFill(RED);
            auctionCreateResult.setText("[Error]: Please fill all required fields");
            return;
        }

        // Create the Background Task
        Task<Void> createTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (!(SessionManager.getCurrentUser() instanceof User seller)) {
                    throw new IllegalArgumentException("[Error]: Session expired! Please log in again.");
                }

                LocalTime startTime = LocalTime.parse(sTimeStr);
                LocalTime endTime = LocalTime.parse(eTimeStr);
                LocalDateTime startFull = startDate.atTime(startTime);
                LocalDateTime endFull = endDate.atTime(endTime);

                Auction auction = auctionApiService.create(new CreateAuctionRequest(
                        selectedItem.getId(), startFull, endFull
                ));

                return null;
            }
        };

        createTask.setOnSucceeded(e -> {
            auctionCreateResult.setTextFill(GREEN);
            auctionCreateResult.setText("Auction created successfully!");
            clearInputs();
        });

        createTask.setOnFailed(e -> {
            Throwable ex = createTask.getException();
            auctionCreateResult.setTextFill(RED);
            if (ex instanceof IllegalArgumentException) {
                auctionCreateResult.setText(ex.getMessage());
            } else if (ex instanceof java.time.format.DateTimeParseException) {
                auctionCreateResult.setText("Invalid Time Format (Use HH:mm)");
            } else {
                auctionCreateResult.setText("Unexpected error: " + ex.getMessage());
            }
        });

        new Thread(createTask).start();
    }

    private void loadAvailableItems() {
        Task<List<Item>> loadTask = new Task<>() {
            @Override
            protected List<Item> call() throws Exception {
                User user = (User) SessionManager.getCurrentUser();
                List<Item> allItems = itemApiService.fetchInventory(user.getId());

                // Filter the list in the UI layer
                return allItems.stream()
                        .filter(item -> item.getStatus() == Item.Status.AVAILABLE)
                        .collect(java.util.stream.Collectors.toList());
            }
        };

        loadTask.setOnSucceeded(e -> {
            itemDropdown.setItems(javafx.collections.FXCollections.observableArrayList(loadTask.getValue()));
        });

        loadTask.setOnFailed(e -> {
            auctionCreateResult.setText("Error loading items: " + loadTask.getException().getMessage());
        });

        new Thread(loadTask).start();
    }

    private void clearInputs() {
        itemNameInput.clear();
        descriptionInput.clear();
        startingPriceInput.clear();
        selectedImageFile = null;
        fileNameLabel.setText("");
        imagePreview.setImage(null);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp"));
        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            fileNameLabel.setText(selectedImageFile.getName());
            imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }
}
