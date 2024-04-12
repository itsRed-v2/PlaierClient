package net.itsred_v2.plaier.ai.pathfinding;

import java.util.function.Consumer;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.utils.Messenger;

public class AsyncPathFinderWrapper implements UpdateListener {

    private final PathFinder pathFinder;
    private final Consumer<PathFinderExitStatus> onComplete;

    public AsyncPathFinderWrapper(PathFinder pathFinder, Consumer<PathFinderExitStatus> onComplete) {
        this.pathFinder = pathFinder;
        this.onComplete = onComplete;

        Thread thread = new Thread(pathFinder::start);

        thread.setUncaughtExceptionHandler((t, e) -> {
            PlaierClient.LOGGER.error("Unhandled error in the pathfinder thread", e);
            Messenger.send("§cAn unhandled error occurred in the pathfinder thread.");

            PlaierClient.getEventManager().remove(UpdateListener.class, this);
            this.onComplete.accept(PathFinderExitStatus.UNHANDLED_ERROR);
        });

        thread.start();

        PlaierClient.getEventManager().add(UpdateListener.class, this);
    }

    @Override
    public void onUpdate() {
        if (pathFinder.isDone()) {
            PlaierClient.getEventManager().remove(UpdateListener.class, this);
            this.onComplete.accept(pathFinder.getExitStatus());
        }
    }
}
