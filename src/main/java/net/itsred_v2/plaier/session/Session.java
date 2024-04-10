package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.task.TaskLifeManager;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;

public class Session {

    private final TaskLifeManager taskManager;
    private final BlockHelper blockHelper;

    private boolean terminated = false;

    public Session() {
        ClientWorld world = getWorld();
        taskManager = new TaskLifeManager();
        blockHelper = new BlockHelper(world);
    }

    @NotNull
    public ClientWorld getWorld() {
        throwIfTerminated();
        // world cannot be null during game
        return Objects.requireNonNull(PlaierClient.MC.world);
    }

    public TaskLifeManager getTaskManager() {
        throwIfTerminated();
        return taskManager;
    }

    public BlockHelper getBlockHelper() {
        throwIfTerminated();
        return blockHelper;
    }

    public void terminate() {
        taskManager.stopTask();
        terminated = true;
    }

    private void throwIfTerminated() {
        if (terminated) {
            throw new UnsupportedOperationException("Trying to use session but it is terminated.");
        }
    }
}
