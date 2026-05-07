package ru.wqkcpf.moderationhelper.chat;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.gui.PunishmentScreen;

import java.nio.file.Path;
import java.util.Optional;

public final class ChatClickHandler {
    private ChatClickHandler() {}

    public static boolean onChatMouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_MIDDLE) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        String message = ChatHistoryCapture.pickByMouseY(mouseY, client.getWindow().getScaledHeight());
        Optional<String> nick = ChatNicknameParser.parseNick(message);
        if (nick.isEmpty()) {
            ModerationHelperClient.chat("Ник не найден в выбранной строке чата.");
            return true;
        }

        Path screenshot = null;
        if (!NoScreenshotKeywords.containsNoScreenshotKeyword(message)) {
            screenshot = ModerationHelperClient.SCREENSHOTS.createTempScreenshot(nick.get());
        }

        ModerationHelperClient.RECENT_PLAYERS.add(nick.get());
        Path finalScreenshot = screenshot;
        client.execute(() -> client.setScreen(new PunishmentScreen(nick.get(), finalScreenshot)));
        return true;
    }
}
