package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.GameModeChangeListener;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "setGameMode", at = @At("HEAD"))
    public void setGameMode(GameMode gameMode, CallbackInfo ci) {
        EventManager.fire(new GameModeChangeListener.GameModeChangeEvent(gameMode));
    }

    @Inject(method = "setGameModes", at = @At("HEAD"))
    public void setGameModes(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        EventManager.fire(new GameModeChangeListener.GameModeChangeEvent(gameMode));
    }

}
