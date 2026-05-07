package ru.wqkcpf.moderationhelper.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.gui.StatsScreen;

public final class KeybindManager {
    private static KeyBinding statsKey;
    private static KeyBinding stopObsKey;

    private KeybindManager() {}

    public static void register() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(ModerationHelperClient.MOD_ID, "main"));

        statsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moderation_helper_gui.stats",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                category
        ));

        stopObsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moderation_helper_gui.stop_obs",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                category
        ));
    }

    public static void tick(MinecraftClient client) {
        if (statsKey == null || stopObsKey == null) return;
        while (statsKey.wasPressed()) {
            client.setScreen(new StatsScreen());
        }

        while (stopObsKey.wasPressed()) {
            // Важное поведение: если открыт чат, G не останавливает запись.
            if (client.currentScreen instanceof ChatScreen) {
                continue;
            }
            ModerationHelperClient.OBS.stopRecording();
            ModerationHelperClient.chat("OBS-запись остановлена.");
        }
    }
}
