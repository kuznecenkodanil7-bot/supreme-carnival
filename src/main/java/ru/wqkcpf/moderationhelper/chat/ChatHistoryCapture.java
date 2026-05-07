package ru.wqkcpf.moderationhelper.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChatHistoryCapture {
    private static final int LIMIT = 80;
    private static final List<String> MESSAGES = new ArrayList<>();

    private ChatHistoryCapture() {}

    public static synchronized void add(String message) {
        if (message == null || message.isBlank()) return;
        MESSAGES.add(0, message);
        while (MESSAGES.size() > LIMIT) MESSAGES.remove(MESSAGES.size() - 1);
    }

    public static synchronized List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(MESSAGES));
    }

    public static synchronized String pickByMouseY(double mouseY, int screenHeight) {
        if (MESSAGES.isEmpty()) return "";
        int chatBottom = screenHeight - 40;
        int lineHeight = 9;
        int index = (int)Math.max(0, Math.floor((chatBottom - mouseY) / lineHeight));
        if (index >= MESSAGES.size()) index = 0;
        return MESSAGES.get(index);
    }
}
