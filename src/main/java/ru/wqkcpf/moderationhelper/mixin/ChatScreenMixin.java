package ru.wqkcpf.moderationhelper.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wqkcpf.moderationhelper.chat.ChatClickHandler;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z", at = @At("HEAD"), cancellable = true)
    private void mhg$middleClickNick(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (ChatClickHandler.onChatMouseClicked(click.x(), click.y(), click.button())) {
            cir.setReturnValue(true);
        }
    }
}
