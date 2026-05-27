package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        Path avatarPath = tempDir.resolve("avatars");
        Path itemPath = tempDir.resolve("items");

        fileStorageService = new FileStorageService(
                avatarPath.toString(),
                itemPath.toString()
        );
    }

    @Test
    @DisplayName("Save avatar successfully with valid image format")
    void saveAvatar_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        String savedName = fileStorageService.saveAvatar(file, 99);

        assertNotNull(savedName);
        assertTrue(savedName.contains("avatar_"));
        assertTrue(savedName.endsWith(".png"));
    }

    @Test
    @DisplayName("Throw exception when saving file with unsupported extension")
    void saveAvatar_WrongExtension_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () ->
                fileStorageService.saveAvatar(file, 99)
        );
    }
}