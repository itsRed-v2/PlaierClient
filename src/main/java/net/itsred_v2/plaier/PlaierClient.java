package net.itsred_v2.plaier;

import net.fabricmc.api.ClientModInitializer;
import net.itsred_v2.plaier.command.CommandProcessor;
import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaierClient implements ClientModInitializer {

    public static final String MOD_ID = "plaier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final EventManager eventManager = new EventManager();

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Plaier is playing !");

        eventManager.add(ChatOutputListener.class, new CommandProcessor());
    }

    public static EventManager getEventManager() {
        return eventManager;
    }
}
