package net.itsred_v2.plaier.mixin;

import com.mojang.authlib.GameProfile;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.AutoJumpListener;
import net.itsred_v2.plaier.events.UpdateListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
                    ordinal = 0))
    public void onUpdate(CallbackInfo ci) {
        PlaierClient.getEventManager().fire(UpdateListener.UpdateEvent.INSTANCE);
    }

    @Inject(method = "autoJump",
            at = @At("HEAD"),
            cancellable = true)
    public void autoJump(float dx, float dz, CallbackInfo ci) {
        AutoJumpListener.AutoJumpEvent event = new AutoJumpListener.AutoJumpEvent();
        PlaierClient.getEventManager().fire(event);

        if (event.isCancelled())
            ci.cancel();
    }

}
