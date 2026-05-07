package ru.wqkcpf.moderationhelper.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

public final class StatsScreen extends BaseModScreen {
    private int recentX;
    private int recentY;
    private int recentW;

    public StatsScreen() {
        super("Панель модератора");
    }

    @Override
    protected void init() {
        int x = width / 2 - 240;
        int y = height / 2 - 130;
        recentX = x + 24;
        recentY = y + 115;
        recentW = 432;
        addDrawableChild(button(x + 150, y + 260, 180, 24, "Закрыть", b -> close()));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (handleRecentClick(click.x(), click.y(), recentX, recentY, recentW)) return true;
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int x = width / 2 - 240;
        int y = height / 2 - 150;
        renderPanel(context, x, y, 480, 310);
        renderTitle(context, "Moderation Helper GUI — панель", x + 24, y + 18);
        context.drawTextWithShadow(textRenderer, "H открывает только эту панель. Ник из чата не берётся, скрин не делается.", x + 24, y + 38, 0xFFBBBBBB);
        renderStats(context, x + 24, y + 60);
        renderRecent(context, recentX, recentY, recentW, true);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
