package ru.wqkcpf.moderationhelper.gui;

import net.minecraft.client.gui.DrawContext;
import ru.wqkcpf.moderationhelper.punishment.PunishmentDraft;
import ru.wqkcpf.moderationhelper.punishment.RuleReason;
import ru.wqkcpf.moderationhelper.punishment.RuleRegistry;

import java.util.List;

public final class ReasonSelectScreen extends BaseModScreen {
    private final PunishmentDraft draft;
    private final List<RuleReason> reasons;

    public ReasonSelectScreen(PunishmentDraft draft) {
        super("Выбор причины");
        this.draft = draft;
        this.reasons = RuleRegistry.reasonsFor(draft.type);
    }

    @Override
    protected void init() {
        int x = width / 2 - 210;
        int y = 54;
        int buttonW = 420;
        int buttonH = 22;
        int gap = 4;

        for (int i = 0; i < reasons.size(); i++) {
            RuleReason reason = reasons.get(i);
            int yy = y + i * (buttonH + gap);
            addDrawableChild(button(x, yy, buttonW, buttonH, reason.display(), b -> choose(reason)));
        }

        addDrawableChild(button(width / 2 - 80, height - 34, 160, 22, "Назад", b -> client.setScreen(new PunishmentScreen(draft.nick, draft.tempScreenshot))));
    }

    private void choose(RuleReason reason) {
        draft.reason = reason;
        if (reason.permanent() && reason.durationPresets().contains("perm")) {
            draft.duration = "perm";
            client.setScreen(new ReasonInputScreen(draft));
        } else {
            client.setScreen(new DurationScreen(draft));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int panelW = 470;
        int panelH = Math.min(height - 30, 82 + reasons.size() * 26);
        int x = (width - panelW) / 2;
        int y = 20;
        renderPanel(context, x, y, panelW, panelH);
        renderTitle(context, draft.type.label + " для " + draft.nick, x + 24, y + 16);
        context.drawTextWithShadow(textRenderer, "Выбери причину", x + 24, y + 32, 0xFFFFDD55);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
