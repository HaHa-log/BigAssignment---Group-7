package controllers.AuctionPage;

import models.Auction;
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

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class AuctionCreateController {
    @FXML private TextField itemNameInput, descriptionInput, startingPriceInput;
    @FXML private DatePicker startingDateInput, endingDateInput;
    @FXML private TextField startingTimeInput, endingTimeInput;
    @FXML private Label fileNameLabel, auctionCreateResult;
    @FXML private ImageView imagePreview;

    private final AuctionApiService auctionApiService = new AuctionApiService();
    private final ItemApiService itemApiService = new ItemApiService();

    private File selectedImageFile;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    @FXML
    private void createAuction() {
        String itemName = itemNameInput.getText();
        String description = descriptionInput.getText();
        String startingPriceRaw = startingPriceInput.getText();
        LocalDate startDate = startingDateInput.getValue();
        LocalDate endDate = endingDateInput.getValue();
        String sTimeStr = startingTimeInput.getText();
        String eTimeStr = endingTimeInput.getText();

        // Basic validation
        if (itemName.trim().isEmpty() || startingPriceRaw.trim().isEmpty() || startDate == null || endDate == null) {
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

                double startingPrice = Double.parseDouble(startingPriceRaw);
                LocalTime startTime = LocalTime.parse(sTimeStr);
                LocalTime endTime = LocalTime.parse(eTimeStr);
                LocalDateTime startFull = startDate.atTime(startTime);
                LocalDateTime endFull = endDate.atTime(endTime);

                Auction auction = auctionApiService.create(new CreateAuctionRequest(
                        seller.getId(), itemName, description,
                        startingPrice, startFull, endFull,
                        null
                ));

                if (selectedImageFile == null) {
                    return null; // No image to upload, just return
                } else {
                    itemApiService.uploadItemImage(auction.getItem().getId(), selectedImageFile);
                }

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
            } else if (ex instanceof IOException) {
                auctionCreateResult.setText("File Error: Could not save image.");
            } else if (ex instanceof java.time.format.DateTimeParseException) {
                auctionCreateResult.setText("Invalid Time Format (Use HH:mm)");
            } else {
                auctionCreateResult.setText("Unexpected error: " + ex.getMessage());
            }
        });

        new Thread(createTask).start();
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            fileNameLabel.setText(selectedImageFile.getName());
            imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }
}
