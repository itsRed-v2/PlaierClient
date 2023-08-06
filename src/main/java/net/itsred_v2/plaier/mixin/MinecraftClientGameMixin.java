package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.LeaveGameSessionListener.LeaveGameSessionEvent;
import net.itsred_v2.plaier.events.StartGameSessionListener.StartGameSessionEvent;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClientGame.class)
public class MinecraftClientGameMixin {

    @Inject(method = "onStartGameSession",
            at = @At("HEAD"))
    public void onStartGameSession(CallbackInfo ci) {
        EventManager.fire(new StartGameSessionEvent());
    }

    @Inject(method = "onLeaveGameSession",
            at = @At("HEAD"))
    public void onLeaveGameSession(CallbackInfo ci) {
        EventManager.fire(new LeaveGameSessionEvent());
    }
    
}
