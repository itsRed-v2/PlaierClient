package net.itsred_v2.plaier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.itsred_v2.plaier.command.CommandProcessor;
import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener.BeforeDebugRenderEvent;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.events.LeaveGameSessionListener;
import net.itsred_v2.plaier.events.StartGameSessionListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.session.SessionLifeManager;
import net.itsred_v2.plaier.utils.MCInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaierClient implements ClientModInitializer {

    private static final String MOD_ID = "plaier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MCInterface MC = new MCInterface(MinecraftClient.getInstance());

    private static final EventManager EVENT_MANAGER = new EventManager();
    private static final SessionLifeManager SESSION_MANAGER = new SessionLifeManager();

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Plaier is playing !");

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> EventManager.fire(new BeforeDebugRenderEvent(context)));
        EVENT_MANAGER.add(ChatOutputListener.class, new CommandProcessor());
        EVENT_MANAGER.add(StartGameSessionListener.class, SESSION_MANAGER);
        EVENT_MANAGER.add(LeaveGameSessionListener.class, SESSION_MANAGER);
    }

    public static EventManager getEventManager() {
        return EVENT_MANAGER;
    }

    public static Session getCurrentSession() {
        return SESSION_MANAGER.getCurrentSession();
    }

    public static ClientPlayerEntity getPlayer() {
        return MC.getPlayer();
    }

}
