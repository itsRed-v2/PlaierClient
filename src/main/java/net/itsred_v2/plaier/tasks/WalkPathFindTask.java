package net.itsred_v2.plaier.tasks;

import java.util.List;
import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.PathFinderResult;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.WalkPathFinder;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.rendering.PolylineRenderer;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.tasks.pathProcessing.PathProcessorResult;
import net.itsred_v2.plaier.tasks.pathProcessing.WalkPathProcessor;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class WalkPathFindTask implements Task, UpdateListener {

    private final BlockPos goal;
    private PolylineRenderer pathRenderer;
    private WalkPathFinder pathFinder;
    private WalkPathProcessor pathProcessor;
    private TaskState state = TaskState.READY;
    private boolean pathFinderDone = false;

    public WalkPathFindTask(BlockPos goal) {
        this.goal = goal;
    }

    @Override
    public void start() {
        if (state != TaskState.READY) throw new RuntimeException("Task started twice.");
        state = TaskState.RUNNING;

        PlaierClient.getEventManager().add(UpdateListener.class, this);
        pathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 255, 100, 0));
        pathRenderer.enable();

        startPathFinding();
    }

    private void startPathFinding() {
        Session session = PlaierClient.getCurrentSession();
        Messenger messenger = session.getMessenger();
        BlockPos start = session.getPlayer().getBlockPos();

        pathFinder = new WalkPathFinder(start, goal, session);
        pathFinderDone = false;

        messenger.send("Searching path...");
        new Thread(() -> {
            try {
                pathFinder.start();
            } catch (Exception e) {
                PlaierClient.LOGGER.error(e.getMessage());
                messenger.send("§cAn error occurred in the pathfinder algorithm.");
                terminate();
            }
        }).start();
    }

    @Override
    public void terminate() {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        pathRenderer.disable();

        if (!pathFinder.isDone())
            pathFinder.stop();

        if (pathProcessor != null)
            pathProcessor.terminate();
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    @Override
    public void onUpdate() {
        if (!pathFinderDone) {
            if (pathFinder.isDone()) {
                pathFinderDone = true;
                onPathFinderDone();
            } else {
                setRenderedPath(pathFinder.traceCurrentPathPositions());
            }
        }
    }

    private void onPathFinderDone() {
        Messenger messenger = PlaierClient.getCurrentSession().getMessenger();

        PathFinderResult result = Objects.requireNonNull(pathFinder.getResult());
        switch (result) {
            case FOUND -> {
                setRenderedPath(pathFinder.traceCurrentPathPositions());
                messenger.send("Path found in %d ms.", pathFinder.getCalculationTime());
                startPathProcessor();
            }
            case INVALID_START -> {
                messenger.send("§cError: §fInaccessible starting position.");
                terminate();
            }
            case INVALID_GOAL -> {
                messenger.send("§cError: §fInaccessible goal position.");
                terminate();
            }
            case REACHED_ITERATION_LIMIT -> {
                messenger.send("§cReached iteration limit: could not find a path to the goal. It may be unreachable or too far away.");
                terminate();
            }
            case TRAPPED -> {
                messenger.send("§cThe goal is unreachable. The pathfinder ran out of paths to explore.");
                terminate();
            }
            default -> {}
        }
    }

    private void setRenderedPath(List<BlockPos> path) {
        pathRenderer.vertices = path.stream()
                .map(blockPos -> new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5))
                .toList();
    }

    private void startPathProcessor() {
        pathProcessor = new WalkPathProcessor(pathFinder, this::onPathProcessorDone);
        pathProcessor.start();
    }

    private void onPathProcessorDone(PathProcessorResult result) {
        switch (result) {
            case ARRIVED -> {
                PlaierClient.getCurrentSession().getMessenger()
                        .send("§aSuccessfully arrived at %d %d %d. Releasing controls.", goal.getX(), goal.getY(), goal.getZ());
                terminate();
            }
            case OFF_PATH -> {
                PlaierClient.getCurrentSession().getMessenger()
                        .send("§6Player is off path: Restarting task...");
                pathProcessor = null;
                startPathFinding();
            }
            case INVALID_PATH -> {
                PlaierClient.getCurrentSession().getMessenger()
                        .send("§6The path became invalid: Restarting task...");
                pathProcessor = null;
                startPathFinding();
            }
            default -> {}
        }
    }

}
