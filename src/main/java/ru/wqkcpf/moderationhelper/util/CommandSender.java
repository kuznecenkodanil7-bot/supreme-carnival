package ru.wqkcpf.moderationhelper.util;

import net.minecraft.client.MinecraftClient;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;

public final class CommandSender {
    private CommandSender() {}

    public static void sendCommand(String commandWithSlash) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            ModerationHelperClient.chat("Команда не отправлена: игрок не в мире.");
            return;
        }
        String command = commandWithSlash.startsWith("/") ? commandWithSlash.substring(1) : commandWithSlash;
        try {
            client.getNetworkHandler().sendChatCommand(command);
        } catch (Throwable t) {
            ModerationHelperClient.LOGGER.error("Cannot send command: {}", commandWithSlash, t);
            ModerationHelperClient.chat("Ошибка отправки команды: " + commandWithSlash);
        }
    }

    public static String applyNick(String template, String nick) {
        return template.replace("{nick}", nick);
    }
}
