package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
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
        PlaierClient.LOGGER.info("Message sent: " + message);

        if (message.startsWith("echo ")) {
            info.cancel();
            PlaierClient.MC.player.sendMessage(Text.of(message.substring(5)), false);
        }

    }
}
