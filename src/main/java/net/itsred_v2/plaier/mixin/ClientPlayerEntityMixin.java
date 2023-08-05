package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.ChatOutputListener.ChatOutputEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "sendChatMessage",
            at = @At("HEAD"),
            cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo info) {
        ChatOutputEvent event = new ChatOutputEvent(message);
        EventManager.fire(event);

        if (event.isCancelled()) {
            info.cancel();
        }

    }
}
