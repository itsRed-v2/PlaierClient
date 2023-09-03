package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.task.TaskLifeManager;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;

public class Session {

    private final Messenger messenger;
    private final RotationHelper rotationHelper;
    private final TaskLifeManager taskManager;
    private final BlockHelper blockHelper;

    private boolean terminated = false;

    public Session() {
        ClientPlayerEntity player = getPlayer();
        ClientWorld world = getWorld();
        messenger = new Messenger(player);
        rotationHelper = new RotationHelper(player);
        taskManager = new TaskLifeManager();
        blockHelper = new BlockHelper(world);
    }

    @NotNull
    public ClientPlayerEntity getPlayer() {
        throwIfTerminated();
        // player cannot not be null during game
        return Objects.requireNonNull(PlaierClient.MC.player);
    }

    @NotNull
    public ClientWorld getWorld() {
        throwIfTerminated();
        // world cannot be null during game
        return Objects.requireNonNull(PlaierClient.MC.world);
    }

    public Messenger getMessenger() {
        throwIfTerminated();
        return messenger;
    }

    public RotationHelper getRotationHelper() {
        throwIfTerminated();
        return rotationHelper;
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
