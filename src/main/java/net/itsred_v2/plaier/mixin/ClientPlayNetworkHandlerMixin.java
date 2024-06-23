package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.PlayerDeathListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin",
            at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        EventManager.fire(new StartGameSessionListener.StartGameSessionEvent());
    }

    @Inject(method = "onDeathMessage",
            at = @At("HEAD"))
    public void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        EventManager.fire(PlayerDeathListener.PlayerDeathEvent.INSTANCE);
    }

}
