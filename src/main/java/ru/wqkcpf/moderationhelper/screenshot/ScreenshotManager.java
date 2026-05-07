package ru.wqkcpf.moderationhelper.screenshot;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.config.CleanupMode;
import ru.wqkcpf.moderationhelper.config.ModConfig;
import ru.wqkcpf.moderationhelper.punishment.PunishmentType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

public final class ScreenshotManager {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss", Locale.ROOT);

    private final ModConfig config;
    private final Path root;

    public ScreenshotManager(ModConfig config) {
        this.config = config;
        this.root = FabricLoader.getInstance().getGameDir().resolve(config.screenshotFolder);
    }

    public void ensureFolders() {
        try {
            Files.createDirectories(root.resolve("temp"));
            Files.createDirectories(root.resolve("warn"));
            Files.createDirectories(root.resolve("mute"));
            Files.createDirectories(root.resolve("ban"));
            Files.createDirectories(root.resolve("ipban"));
            Files.createDirectories(root.resolve("archive"));
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.error("Cannot create screenshot folders", e);
        }
    }

    public Path createTempScreenshot(String nick) {
        ensureFolders();
        Path file = root.resolve("temp").resolve(sanitize(nick) + "_" + now() + ".png");
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            ScreenshotRecorder.takeScreenshot(client.getFramebuffer(), image -> saveNativeImage(image, file));
            return file;
        } catch (Throwable t) {
            ModerationHelperClient.LOGGER.error("Cannot take screenshot", t);
            ModerationHelperClient.chat("Скриншот не сделан, но меню откроется.");
            return null;
        }
    }

    private void saveNativeImage(NativeImage image, Path file) {
        try (image) {
            Files.createDirectories(file.getParent());
            image.writeTo(file);
            ModerationHelperClient.LOGGER.info("Saved temp screenshot: {}", file);
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.error("Cannot write screenshot: {}", file, e);
        }
    }

    public void finalizeScreenshot(Path temp, String nick, PunishmentType type, String duration, String reason) {
        if (temp == null || !Files.exists(temp)) return;
        try {
            Path dir = root.resolve(type.id);
            Files.createDirectories(dir);
            String fileName = sanitize(nick) + "_" + type.id + "_" + sanitize(emptyToNone(duration)) + "_" + sanitize(emptyToNone(reason)) + "_" + now() + ".png";
            Files.move(temp, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.error("Cannot move screenshot", e);
            ModerationHelperClient.chat("Не получилось перенести скриншот: " + e.getMessage());
        }
    }

    public void cleanupOldScreenshots() {
        if (config.screenshotCleanupMode == CleanupMode.OFF) return;
        ensureFolders();
        Instant border = Instant.now().minus(Duration.ofDays(Math.max(1, config.screenshotRetentionDays)));
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                    .filter(path -> isOlderThan(path, border))
                    .forEach(this::cleanupOne);
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.error("Screenshot cleanup failed", e);
        }
    }

    private boolean isOlderThan(Path path, Instant border) {
        try {
            return Files.getLastModifiedTime(path).toInstant().isBefore(border);
        } catch (IOException e) {
            return false;
        }
    }

    private void cleanupOne(Path path) {
        try {
            if (config.screenshotCleanupMode == CleanupMode.DELETE) {
                Files.deleteIfExists(path);
            } else if (config.screenshotCleanupMode == CleanupMode.ARCHIVE) {
                Path archive = root.resolve("archive").resolve(path.getFileName());
                Files.createDirectories(archive.getParent());
                Files.move(path, archive, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.warn("Cannot cleanup screenshot {}", path, e);
        }
    }

    public Path root() {
        return root;
    }

    public static String now() {
        return LocalDateTime.now().format(FORMAT);
    }

    public static String sanitize(String value) {
        if (value == null || value.isBlank()) return "none";
        return value.replaceAll("[^A-Za-zА-Яа-я0-9_.-]+", "_").replaceAll("_+", "_");
    }

    private static String emptyToNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
