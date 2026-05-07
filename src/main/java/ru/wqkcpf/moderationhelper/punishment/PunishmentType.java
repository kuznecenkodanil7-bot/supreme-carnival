package ru.wqkcpf.moderationhelper.punishment;

public enum PunishmentType {
    WARN("warn", "Warn", "/warn"),
    MUTE("mute", "Mute", "/mute"),
    BAN("ban", "Ban", "/ban"),
    IPBAN("ipban", "IPBan", "/ipban");

    public final String id;
    public final String label;
    public final String command;

    PunishmentType(String id, String label, String command) {
        this.id = id;
        this.label = label;
        this.command = command;
    }
}
