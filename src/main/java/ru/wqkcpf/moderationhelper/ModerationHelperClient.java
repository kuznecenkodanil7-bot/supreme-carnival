package ru.wqkcpf.moderationhelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wqkcpf.moderationhelper.config.ModConfig;
import ru.wqkcpf.moderationhelper.keybind.KeybindManager;
import ru.wqkcpf.moderationhelper.obs.ObsController;
import ru.wqkcpf.moderationhelper.obs.RecordingTimer;
import ru.wqkcpf.moderationhelper.recent.RecentPlayersManager;
import ru.wqkcpf.moderationhelper.screenshot.ScreenshotManager;
import ru.wqkcpf.moderationhelper.stats.SessionStats;

public final class ModerationHelperClient implements ClientModInitializer {
    public static final String MOD_ID = "moderation_helper_gui";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfig CONFIG;
    public static SessionStats STATS;
    public static RecentPlayersManager RECENT_PLAYERS;
    public static ScreenshotManager SCREENSHOTS;
    public static ObsController OBS;
    public static RecordingTimer RECORDING_TIMER;

    @Override
    public void onInitializeClient() {
        CONFIG = ModConfig.load();
        STATS = new SessionStats();
        RECENT_PLAYERS = new RecentPlayersManager(CONFIG.recentPlayersLimit);
        SCREENSHOTS = new ScreenshotManager(CONFIG);
        OBS = new ObsController(CONFIG);
        RECORDING_TIMER = new RecordingTimer();

        SCREENSHOTS.ensureFolders();
        SCREENSHOTS.cleanupOldScreenshots();

        KeybindManager.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindManager.tick(client);
            RECORDING_TIMER.tick(client);
        });

        LOGGER.info("Moderation Helper GUI loaded");
    }

    public static void chat(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§8[§bMHG§8] §f" + message), false);
        }
        LOGGER.info(message);
    }
}
