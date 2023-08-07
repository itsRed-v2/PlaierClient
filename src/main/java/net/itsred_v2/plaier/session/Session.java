package net.itsred_v2.plaier.session;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.task.TaskLifeManager;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class Session {

    private final Messenger messenger;
    private final RotationHelper rotationHelper;
    private final TaskLifeManager taskManager;

    public Session() {
        ClientPlayerEntity player = getPlayer();
        messenger = new Messenger(player);
        rotationHelper = new RotationHelper(player);
        taskManager = new TaskLifeManager();
    }

    @NotNull
    public ClientPlayerEntity getPlayer() {
        return Objects.requireNonNull(PlaierClient.MC.player); // player cannot not be null during game
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public RotationHelper getRotationHelper() {
        return rotationHelper;
    }

    public TaskLifeManager getTaskManager() {
        return taskManager;
    }

    public void terminate() {
        taskManager.stopTask();
    }
}
