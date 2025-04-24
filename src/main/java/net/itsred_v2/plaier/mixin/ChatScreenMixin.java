package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.mixinterface.ChatScreenInterface;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen implements ChatScreenInterface {

    @Shadow
    protected TextFieldWidget chatField;

    private ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "sendMessage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V",
                    ordinal = 0),
            cancellable = true)
    public void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        ChatOutputListener.ChatOutputEvent event = new ChatOutputListener.ChatOutputEvent(chatText);
        PlaierClient.getEventManager().fire(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Unique
    @Override
    public String plaierClient$getChatFieldText() {
        return chatField.getText();
    }

}
