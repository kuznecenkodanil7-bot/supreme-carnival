package ru.wqkcpf.moderationhelper.stats;

import ru.wqkcpf.moderationhelper.punishment.PunishmentType;

import java.util.EnumMap;
import java.util.Map;

public final class SessionStats {
    private final Map<PunishmentType, Integer> counts = new EnumMap<>(PunishmentType.class);

    public SessionStats() {
        for (PunishmentType type : PunishmentType.values()) {
            counts.put(type, 0);
        }
    }

    public void increment(PunishmentType type) {
        counts.put(type, get(type) + 1);
    }

    public int get(PunishmentType type) {
        return counts.getOrDefault(type, 0);
    }
}
