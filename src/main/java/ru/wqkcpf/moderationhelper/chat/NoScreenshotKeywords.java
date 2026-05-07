package ru.wqkcpf.moderationhelper.chat;

import java.util.List;
import java.util.Locale;

public final class NoScreenshotKeywords {
    private static final List<String> KEYWORDS = List.of(
            "tick speed",
            "reach",
            "fighting suspiciously",
            "block interaction"
    );

    private NoScreenshotKeywords() {}

    public static boolean containsNoScreenshotKeyword(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.ROOT);
        for (String keyword : KEYWORDS) {
            if (lower.contains(keyword)) return true;
        }
        return false;
    }
}
