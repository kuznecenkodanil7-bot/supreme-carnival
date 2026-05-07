package ru.wqkcpf.moderationhelper.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import ru.wqkcpf.moderationhelper.punishment.PunishmentDraft;

public final class ReasonInputScreen extends BaseModScreen {
    private final PunishmentDraft draft;
    private TextFieldWidget input;

    public ReasonInputScreen(PunishmentDraft draft) {
        super("Ввод причины");
        this.draft = draft;
    }

    @Override
    protected void init() {
        int x = width / 2 - 210;
        int y = height / 2 - 95;
        input = new TextFieldWidget(textRenderer, x + 20, y + 72, 380, 22, Text.literal("reason"));
        input.setMaxLength(80);
        input.setText(draft.reason.code());
        addDrawableChild(input);
        setInitialFocus(input);

        addDrawableChild(button(x + 20, y + 102, 118, 22, draft.reason.code(), b -> input.setText(draft.reason.code())));
        addDrawableChild(button(x + 144, y + 102, 118, 22, draft.reason.title(), b -> input.setText(draft.reason.title())));
        addDrawableChild(button(x + 268, y + 102, 132, 22, "код + текст", b -> input.setText(draft.reason.display())));

        int quickY = y + 132;
        int i = 0;
        for (String quick : ru.wqkcpf.moderationhelper.ModerationHelperClient.CONFIG.quickCustomReasons) {
            if (i >= 6) break;
            addDrawableChild(button(x + 20 + (i % 3) * 126, quickY + (i / 3) * 24, 118, 20, quick, b -> input.setText(quick)));
            i++;
        }

        addDrawableChild(button(x + 20, y + 184, 185, 24, "Выдать наказание", b -> confirm()));
        addDrawableChild(button(x + 215, y + 184, 185, 24, "Назад", b -> client.setScreen(new DurationScreen(draft))));
    }

    private void confirm() {
        String value = input.getText().trim();
        if (!value.isBlank()) {
            draft.reason = new ru.wqkcpf.moderationhelper.punishment.RuleReason(value, value, draft.reason.durationPresets(), draft.reason.permanent());
        }
        PunishmentExecutor.execute(draft);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int x = width / 2 - 220;
        int y = height / 2 - 120;
        renderPanel(context, x, y, 440, 240);
        renderTitle(context, "Причина наказания", x + 24, y + 18);
        context.drawTextWithShadow(textRenderer, "Ник: " + draft.nick, x + 24, y + 38, 0xFFFFDD55);
        context.drawTextWithShadow(textRenderer, "Тип: " + draft.type.label + "   Время: " + draft.duration, x + 24, y + 54, 0xFFFFFFFF);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
