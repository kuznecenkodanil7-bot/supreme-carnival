package ru.wqkcpf.moderationhelper.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.punishment.PunishmentDraft;
import ru.wqkcpf.moderationhelper.punishment.PunishmentType;
import ru.wqkcpf.moderationhelper.punishment.RuleRegistry;
import ru.wqkcpf.moderationhelper.util.CommandSender;

import java.nio.file.Path;

public final class PunishmentScreen extends BaseModScreen {
    private final String nick;
    private final Path tempScreenshot;
    private int recentX;
    private int recentY;
    private int recentW;

    public PunishmentScreen(String nick, Path tempScreenshot) {
        super("Moderation Helper GUI");
        this.nick = nick;
        this.tempScreenshot = tempScreenshot;
    }

    @Override
    protected void init() {
        int panelW = 560;
        int panelH = 310;
        int x = (width - panelW) / 2;
        int y = Math.max(20, (height - panelH) / 2);
        int buttonW = 170;
        int buttonH = 24;
        int bx = x + 24;
        int by = y + 58;

        addDrawableChild(button(bx, by, buttonW, buttonH, "Warn — 2.1 сразу", b -> issueWarn()));
        addDrawableChild(button(bx, by + 32, buttonW, buttonH, "Mute", b -> openReasons(PunishmentType.MUTE)));
        addDrawableChild(button(bx, by + 64, buttonW, buttonH, "Ban", b -> openReasons(PunishmentType.BAN)));
        addDrawableChild(button(bx, by + 96, buttonW, buttonH, "IPBan", b -> openReasons(PunishmentType.IPBAN)));
        addDrawableChild(button(bx, by + 138, buttonW, buttonH, "Вызвать на проверку", b -> callCheck()));
        addDrawableChild(button(bx, by + 170, buttonW, buttonH, "Снять с проверки", b -> stopCheck()));

        recentX = x + 220;
        recentY = y + 142;
        recentW = 310;
    }

    private void issueWarn() {
        PunishmentDraft draft = new PunishmentDraft(nick, PunishmentType.WARN, tempScreenshot);
        draft.reason = RuleRegistry.reasonsFor(PunishmentType.WARN).get(0);
        draft.duration = "";
        PunishmentExecutor.execute(draft);
        close();
    }

    private void openReasons(PunishmentType type) {
        client.setScreen(new ReasonSelectScreen(new PunishmentDraft(nick, type, tempScreenshot)));
    }

    private void callCheck() {
        CommandSender.sendCommand(CommandSender.applyNick(ModerationHelperClient.CONFIG.tppCommandTemplate, nick));
        CommandSender.sendCommand(CommandSender.applyNick(ModerationHelperClient.CONFIG.tpCommandTemplate, nick));
        CommandSender.sendCommand(CommandSender.applyNick(ModerationHelperClient.CONFIG.checkCommandTemplate, nick));
        CommandSender.sendCommand(CommandSender.applyNick(ModerationHelperClient.CONFIG.checkTellTemplate, nick));
        ModerationHelperClient.OBS.startRecording();
        ModerationHelperClient.RECENT_PLAYERS.add(nick);
        ModerationHelperClient.chat("Игрок вызван на проверку: " + nick);
    }

    private void stopCheck() {
        ModerationHelperClient.OBS.stopRecording();
        ModerationHelperClient.chat("Проверка остановлена: " + nick);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (handleRecentClick(click.x(), click.y(), recentX, recentY, recentW)) return true;
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int panelW = 560;
        int panelH = 310;
        int x = (width - panelW) / 2;
        int y = Math.max(20, (height - panelH) / 2);
        renderPanel(context, x, y, panelW, panelH);
        renderTitle(context, "Moderation Helper GUI", x + 24, y + 18);
        context.drawTextWithShadow(textRenderer, "Ник: " + nick, x + 24, y + 36, 0xFFFFDD55);
        context.drawTextWithShadow(textRenderer, "Кого наказать: " + nick, x + 220, y + 114, 0xFFFFFFFF);
        renderStats(context, x + 390, y + 18);
        renderRecent(context, recentX, recentY, recentW, true);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
