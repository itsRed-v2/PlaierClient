package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.ChangeLookDirectionListener;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {

    @Redirect(method = "updateMouse",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void changeLookDirectionRedirect(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        ChangeLookDirectionListener.ChangeLookDirectionEvent event = new ChangeLookDirectionListener.ChangeLookDirectionEvent(cursorDeltaX, cursorDeltaY);
        EventManager.fire(event);

        if (!event.isCancelled()) {
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }

}
