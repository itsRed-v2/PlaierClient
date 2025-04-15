package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.AttackBlockListener;
import net.itsred_v2.plaier.events.GameModeChangeListener;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow
    private GameMode gameMode = GameMode.DEFAULT;

    @Inject(method = "setGameMode", at = @At("HEAD"))
    public void setGameMode(GameMode gameMode, CallbackInfo ci) {
        EventManager.fire(new GameModeChangeListener.GameModeChangeEvent(gameMode));
    }

    @Inject(method = "setGameModes", at = @At("HEAD"))
    public void setGameModes(GameMode gameMode, GameMode previousGameMode, CallbackInfo ci) {
        EventManager.fire(new GameModeChangeListener.GameModeChangeEvent(gameMode));
    }

    @Inject(method = "attackBlock", at = @At("HEAD"))
    public void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (PlaierClient.getPlayer().isBlockBreakingRestricted(PlaierClient.MC.getClientWorld(), pos, this.gameMode))
            return;
        if (!PlaierClient.MC.getClientWorld().getWorldBorder().contains(pos))
            return;

        EventManager.fire(new AttackBlockListener.AttackBlockEvent(pos));
    }

}
