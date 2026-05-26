package utils;

import java.io.File;
import java.util.Set;

public final class ImageFileValidator {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private ImageFileValidator() {
    }

    public static void validate(File file) {
        if (file == null) {
            return;
        }

        if (file.length() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image too large. Max 5MB.");
        }

        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        String ext = dotIndex >= 0 ? name.substring(dotIndex + 1).toLowerCase() : "";

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Only jpg, jpeg, png, webp are allowed.");
        }
    }
}
