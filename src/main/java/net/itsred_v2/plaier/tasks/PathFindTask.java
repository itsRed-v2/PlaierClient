package net.itsred_v2.plaier.tasks;

import java.util.List;
import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.FlyPathFinder;
import net.itsred_v2.plaier.ai.pathfinding.PathFinderResult;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.rendering.PolylineRenderer;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PathFindTask implements Task, UpdateListener {

    private PolylineRenderer pathRenderer;
    private FlyPathFinder pathFinder;
    private boolean running = false;
    private boolean pathFinderDone = false;
    private boolean done = false;

    private final BlockPos goal;

    public PathFindTask(BlockPos goal) {
        this.goal = goal;
    }

    @Override
    public void start() {
        if (running) throw new RuntimeException("Task started twice.");
        running = true;

        PlaierClient.getEventManager().add(UpdateListener.class, this);

        pathRenderer = new PolylineRenderer();
        pathRenderer.enable();

        Session session = PlaierClient.getCurrentSession();
        Messenger messenger = session.getMessenger();
        BlockPos start = session.getPlayer().getBlockPos();

        pathFinder = new FlyPathFinder(start, goal, session);

        messenger.send("Searching...");

//        new Thread(() -> {
//            try {
                pathFinder.start();
//            } catch (Exception e) {
//                PlaierClient.LOGGER.info(e.getMessage());
//            }
//        }).start();
    }

    @Override
    public void terminate() {
        if (!running) return;
        running = false;

        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        pathRenderer.disable();

        if (!pathFinder.isDone())
            pathFinder.stop();

        if (!done) done = true; // so the sessionManager can clear the task
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void onUpdate() {
        if (!pathFinderDone) {
            if (pathFinder.isDone()) {
                pathFinderDone = true;
                onPathFinderDone();
            } else {
                renderPath(pathFinder.generateUnfinishedPath());
            }
        }
    }

    private void onPathFinderDone() {
        Messenger messenger = PlaierClient.getCurrentSession().getMessenger();

        PathFinderResult result = Objects.requireNonNull(pathFinder.getResult());
        switch (result) {
            case INVALID_START -> {
                messenger.send("§cError: §fInaccessible starting position.");
                terminate();
            }
            case INVALID_GOAL -> {
                messenger.send("§cError: §fInaccessible goal position.");
                terminate();
            }
            case UNLOADED_GOAL -> {
                messenger.send("§cError: §fGoal position is unloaded.");
                terminate();
            }
            case FOUND -> {
                renderPath(Objects.requireNonNull(pathFinder.getPath()));
                messenger.send("§aPath was found.");
            }
            case REACHED_ITERATION_LIMIT -> {
                messenger.send("§cReached iteration limit: could not find path.");
//                terminate();
            }
        }
    }

    private void renderPath(List<BlockPos> path) {
        pathRenderer.vertices = path.stream()
                .map(blockPos -> new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5))
                .toList();
    }

}
