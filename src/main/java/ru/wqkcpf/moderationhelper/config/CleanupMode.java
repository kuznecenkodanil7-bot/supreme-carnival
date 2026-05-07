package ru.wqkcpf.moderationhelper.config;

public enum CleanupMode {
    DELETE,
    ARCHIVE,
    OFF;

    public static CleanupMode fromString(String value) {
        try {
            return CleanupMode.valueOf(value.trim().toUpperCase());
        } catch (Exception ignored) {
            return ARCHIVE;
        }
    }
}
