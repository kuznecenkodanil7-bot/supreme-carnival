package ru.wqkcpf.moderationhelper.obs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class RecordingTimer {
    private boolean running;
    private long startedAt;
    private long lastSecond = -1;

    public void start() {
        running = true;
        startedAt = System.currentTimeMillis();
        lastSecond = -1;
    }

    public void stop() {
        running = false;
        lastSecond = -1;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.inGameHud != null) {
            client.inGameHud.setOverlayMessage(Text.empty(), false);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void tick(MinecraftClient client) {
        if (!running || client.player == null || client.inGameHud == null) return;
        long seconds = Math.max(0, (System.currentTimeMillis() - startedAt) / 1000L);
        if (seconds != lastSecond) {
            lastSecond = seconds;
            long min = seconds / 60L;
            long sec = seconds % 60L;
            client.inGameHud.setOverlayMessage(Text.literal(String.format("Идёт запись: %02d:%02d", min, sec)), false);
        }
    }
}
