package net.itsred_v2.plaier;

import net.fabricmc.api.ClientModInitializer;
import net.itsred_v2.plaier.command.CommandProcessor;
import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.session.SessionManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaierClient implements ClientModInitializer {

    public static final String MOD_ID = "plaier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final EventManager eventManager = new EventManager();
    private static final SessionManager sessionManager = new SessionManager();

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Plaier is playing !");

        eventManager.add(ChatOutputListener.class, new CommandProcessor());
        eventManager.add(StartGameSessionListener.class, sessionManager);
        eventManager.add(LeaveGameSessionListener.class, sessionManager);
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static Session getCurrentSession() {
        return sessionManager.getCurrentSession();
    }

}
