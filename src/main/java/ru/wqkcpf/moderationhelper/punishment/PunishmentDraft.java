package ru.wqkcpf.moderationhelper.punishment;

import java.nio.file.Path;

public final class PunishmentDraft {
    public final String nick;
    public final PunishmentType type;
    public final Path tempScreenshot;
    public RuleReason reason;
    public String duration;

    public PunishmentDraft(String nick, PunishmentType type, Path tempScreenshot) {
        this.nick = nick;
        this.type = type;
        this.tempScreenshot = tempScreenshot;
    }
}
