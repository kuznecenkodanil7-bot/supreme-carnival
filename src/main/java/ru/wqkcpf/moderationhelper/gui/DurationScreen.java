package ru.wqkcpf.moderationhelper.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.punishment.PunishmentDraft;

import java.util.regex.Pattern;

public final class DurationScreen extends BaseModScreen {
    private static final Pattern VALID = Pattern.compile("^(perm|forever|\\d+[dh])$", Pattern.CASE_INSENSITIVE);
    private final PunishmentDraft draft;
    private TextFieldWidget input;
    private String error = "";

    public DurationScreen(PunishmentDraft draft) {
        super("Выбор времени");
        this.draft = draft;
    }

    @Override
    protected void init() {
        int x = width / 2 - 170;
        int y = height / 2 - 78;
        input = new TextFieldWidget(textRenderer, x, y + 40, 340, 22, Text.literal("duration"));
        input.setMaxLength(16);
        if (!draft.reason.durationPresets().isEmpty()) input.setText(draft.reason.durationPresets().get(0));
        addDrawableChild(input);
        setInitialFocus(input);

        int px = x;
        int py = y + 70;
        int i = 0;
        for (String preset : draft.reason.durationPresets()) {
            addDrawableChild(button(px + (i % 4) * 82, py + (i / 4) * 26, 76, 22, preset, b -> input.setText(preset)));
            i++;
        }

        addDrawableChild(button(x, y + 130, 160, 24, "Дальше", b -> next()));
        addDrawableChild(button(x + 180, y + 130, 160, 24, "Назад", b -> client.setScreen(new ReasonSelectScreen(draft))));
    }

    private void next() {
        String value = input.getText().trim();
        if (!VALID.matcher(value).matches()) {
            error = "Формат: 7d, 12h или perm";
            ModerationHelperClient.chat(error);
            return;
        }
        draft.duration = value;
        client.setScreen(new ReasonInputScreen(draft));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        int x = width / 2 - 210;
        int y = height / 2 - 105;
        renderPanel(context, x, y, 420, 210);
        renderTitle(context, "Время наказания", x + 24, y + 18);
        context.drawTextWithShadow(textRenderer, "Ник: " + draft.nick, x + 24, y + 36, 0xFFFFDD55);
        context.drawTextWithShadow(textRenderer, "Причина: " + draft.reason.code(), x + 24, y + 52, 0xFFFFFFFF);
        if (!error.isBlank()) context.drawTextWithShadow(textRenderer, error, x + 24, y + 178, 0xFFFF5555);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
