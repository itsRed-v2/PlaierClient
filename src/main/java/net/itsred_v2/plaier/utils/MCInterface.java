package net.itsred_v2.plaier.utils;

import java.util.Objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.GameMode;

public class MCInterface {

    private final MinecraftClient client;

    public MCInterface(MinecraftClient client) {
        this.client = client;
    }

    public ClientPlayerEntity getPlayer() {
        return Objects.requireNonNull(client.player);
    }

    public ClientWorld getWorld() {
        return Objects.requireNonNull(client.world);
    }

    public TextRenderer getTextRenderer() {
        return client.textRenderer;
    }

    public int getTicks() {
        return client.inGameHud.getTicks();
    }

    public GameOptions getOptions() {
        return client.options;
    }

    public Screen getCurrentScreen() {
        return client.currentScreen;
    }

    public long getWindowHandle() {
        return client.getWindow().getHandle();
    }

    public float getLastFrameDurationInTicks() {
        return client.getRenderTickCounter().getLastFrameDuration();
    }

    public GameMode getGameMode() {
        return Objects.requireNonNull(client.interactionManager).getCurrentGameMode();
    }

    public ClientPlayerInteractionManager getInteractionManager() {
        return Objects.requireNonNull(client.interactionManager);
    }

}
