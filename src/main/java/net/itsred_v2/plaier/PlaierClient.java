package net.itsred_v2.plaier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.itsred_v2.plaier.command.CommandProcessor;
import net.itsred_v2.plaier.command.Commands;
import net.itsred_v2.plaier.event.EventManager;
import net.itsred_v2.plaier.events.BeforeDebugRenderListener.BeforeDebugRenderEvent;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.task.TaskManager;
import net.itsred_v2.plaier.utils.MCInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaierClient implements ClientModInitializer {

    private static final String MOD_ID = "plaier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MCInterface MC = new MCInterface(MinecraftClient.getInstance());
    public static final DebugOptions DEBUG_OPTIONS = new DebugOptions();

    private static final EventManager EVENT_MANAGER = new EventManager();
    private static final TaskManager TASK_MANAGER = new TaskManager();

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        LOGGER.info("Plaier is playing !");

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> EVENT_MANAGER.fire(new BeforeDebugRenderEvent(context)));
        EVENT_MANAGER.add(ChatOutputListener.class, new CommandProcessor());

        Commands.initializeCommandMap();
    }

    public static EventManager getEventManager() {
        return EVENT_MANAGER;
    }

    public static TaskManager getTaskManager() {
        return TASK_MANAGER;
    }

    public static ClientPlayerEntity getPlayer() {
        return MC.getPlayer();
    }

}
