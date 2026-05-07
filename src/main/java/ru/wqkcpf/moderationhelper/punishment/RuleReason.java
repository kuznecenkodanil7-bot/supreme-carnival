package ru.wqkcpf.moderationhelper.punishment;

import java.util.List;

public record RuleReason(String code, String title, List<String> durationPresets, boolean permanent) {
    public String display() {
        if (title == null || title.isBlank()) return code;
        return code + " — " + title;
    }
}
