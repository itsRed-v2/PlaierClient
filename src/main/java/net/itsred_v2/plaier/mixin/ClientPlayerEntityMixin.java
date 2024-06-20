package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.PreUpdateListener;
import net.itsred_v2.plaier.events.UpdateListener;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
                    ordinal = 0))
    public void onUpdate(CallbackInfo ci) {
        // PreUpdateEvent is the same as UpdateEvent, but with higher priority, so it is run first.
        EventManager.fire(PreUpdateListener.PreUpdateEvent.INSTANCE);
        EventManager.fire(UpdateListener.UpdateEvent.INSTANCE);
    }

}
