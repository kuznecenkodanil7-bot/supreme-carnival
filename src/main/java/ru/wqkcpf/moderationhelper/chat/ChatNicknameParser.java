package ru.wqkcpf.moderationhelper.chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class ChatNicknameParser {
    private static final Pattern COLOR_CODES = Pattern.compile("(?i)§[0-9A-FK-OR]|&[0-9A-FK-OR]");
    private static final Pattern MC_NICK = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    private static final Set<String> RANKS = new HashSet<>(Arrays.asList(
            "HT5", "LT5", "HT4", "LT4", "HT3", "LT3", "HT2", "LT2", "HT1", "LT1",
            "RHT3", "RLT3", "RHT2", "RLT2", "RHT1", "RLT1",
            "XHT5", "XLT5", "XHT4", "XLT4", "XHT3", "XLT3", "XHT2", "XLT2", "XHT1", "XLT1",
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    ));

    private static final Set<String> BEFORE_NICK = new HashSet<>(Arrays.asList(
            "anarchy-alpha", "anarchy-beta", "anarchy-gamma", "anarchy-new", "duels"
    ));

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "minecraft", "server", "tick", "speed", "reach", "fighting", "suspiciously", "block", "interaction",
            "warn", "mute", "ban", "ipban", "admin", "moderator", "helper", "global", "local"
    ));

    private ChatNicknameParser() {}

    public static Optional<String> parseNick(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        String clean = clean(raw);
        String[] tokens = clean.split("\\s+");
        boolean nextAfterMarker = false;

        for (String token : tokens) {
            String word = normalizeToken(token);
            if (word.isEmpty()) continue;

            String upper = word.toUpperCase(Locale.ROOT);
            String lower = word.toLowerCase(Locale.ROOT);

            if (RANKS.contains(upper)) {
                nextAfterMarker = true;
                continue;
            }
            if (BEFORE_NICK.contains(lower)) {
                nextAfterMarker = true;
                continue;
            }

            if (nextAfterMarker && isMinecraftNick(word)) return Optional.of(word);
            if (isMinecraftNick(word) && !STOP_WORDS.contains(lower)) return Optional.of(word);
        }
        return Optional.empty();
    }

    public static String clean(String raw) {
        String clean = COLOR_CODES.matcher(raw).replaceAll("");
        clean = clean.replaceAll("[\\[\\]{}()<>«»|/\\\\:;,.!?=+*^%$#@~`'\"→←⟶⟵➜➡-]+", " ");
        return clean.trim();
    }

    public static String normalizeToken(String token) {
        return token.replaceAll("^[^A-Za-z0-9_]+|[^A-Za-z0-9_]+$", "").trim();
    }

    public static boolean isMinecraftNick(String s) {
        return MC_NICK.matcher(s).matches();
    }
}
