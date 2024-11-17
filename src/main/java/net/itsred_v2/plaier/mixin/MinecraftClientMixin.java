package net.itsred_v2.plaier.mixin;

import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.WorldJoinListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {

    private MinecraftClientMixin(String string) {
        super(string);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V",
            at = @At("HEAD"))
    public void disconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
        EventManager.fire(new LeaveGameSessionListener.LeaveGameSessionEvent());
    }

    @Inject(method = "joinWorld",
            at = @At("HEAD"))
    public void joinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        EventManager.fire(WorldJoinListener.WorldJoinEvent.INSTANCE);
    }

}
