package services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private final Path avatarDir;
    private final Path itemDir;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB;

    public FileStorageService(
            @Value("${app.storage.avatars}") String avatarPath,
            @Value("${app.storage.items}") String itemPath
    ) throws IOException {
        this.avatarDir = Paths.get(avatarPath).toAbsolutePath().normalize();
        this.itemDir = Paths.get(itemPath).toAbsolutePath().normalize();
        Files.createDirectories(avatarDir);
        Files.createDirectories(itemDir);
    }

    public String saveAvatar(MultipartFile file, int userId) {
        return save(file, avatarDir, "avatar_" + System.currentTimeMillis() + "_" + userId + "_");
    }

    public String saveItemImage(MultipartFile file, int itemId) {
        return save(file, itemDir, "item_" + System.currentTimeMillis() + "_" + itemId + "_");
    }

    public Path resolveAvatar(String filename) {
        return avatarDir.resolve(filename).normalize();
    }

    public Path resolveItem(String filename) {
        return itemDir.resolve(filename).normalize();
    }

    private String save(MultipartFile file, Path dir, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image too large (Max 5MB)");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = getExtension(original).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Only jpg, jpeg, png, webp are allowed.");
        }

        String storedName = prefix + UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(storedName).normalize();

        // Chặn path traversal
        if (!target.startsWith(dir)) {
            throw new IllegalArgumentException("Invalid file path.");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName; // chỉ trả filename để lưu DB
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx >= 0 ? name.substring(idx + 1) : "";
    }
}