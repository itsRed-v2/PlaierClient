package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.PlayerDeathListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler
        implements TickablePacketListener, ClientPlayPacketListener {

    private ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "onDeathMessage",
            at = @At("HEAD"))
    public void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        // TODO: Check if other player's death triggers this
        PlaierClient.getEventManager().fire(PlayerDeathListener.PlayerDeathEvent.INSTANCE);
    }

}
