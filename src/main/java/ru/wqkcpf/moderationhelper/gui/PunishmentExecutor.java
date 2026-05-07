package ru.wqkcpf.moderationhelper.gui;

import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.punishment.PunishmentDraft;
import ru.wqkcpf.moderationhelper.punishment.PunishmentType;
import ru.wqkcpf.moderationhelper.util.CommandSender;

public final class PunishmentExecutor {
    private PunishmentExecutor() {}

    public static void execute(PunishmentDraft draft) {
        String reason = draft.reason == null ? "" : draft.reason.code();
        String duration = draft.duration == null ? "" : draft.duration.trim();

        String command = switch (draft.type) {
            case WARN -> "/warn " + draft.nick + " " + safeArg(reason);
            case MUTE -> "/mute " + draft.nick + " " + safeArg(duration) + " " + safeArg(reason);
            case BAN -> "/ban " + draft.nick + " " + safeArg(duration) + " " + safeArg(reason);
            case IPBAN -> "/ipban " + draft.nick + " " + safeArg(duration) + " " + safeArg(reason);
        };

        CommandSender.sendCommand(command);
        ModerationHelperClient.STATS.increment(draft.type);
        ModerationHelperClient.RECENT_PLAYERS.add(draft.nick);
        ModerationHelperClient.SCREENSHOTS.finalizeScreenshot(draft.tempScreenshot, draft.nick, draft.type, duration, reason);

        if (draft.type == PunishmentType.IPBAN && shouldStopObsAfterIpBan(reason)) {
            ModerationHelperClient.OBS.stopRecording();
        }

        ModerationHelperClient.chat("Наказание отправлено: " + command);
    }

    private static boolean shouldStopObsAfterIpBan(String reason) {
        String r = reason == null ? "" : reason.trim().toLowerCase();
        return !(r.equals("бот") || r.equals("3.8"));
    }

    private static String safeArg(String value) {
        if (value == null) return "";
        return value.trim();
    }
}
