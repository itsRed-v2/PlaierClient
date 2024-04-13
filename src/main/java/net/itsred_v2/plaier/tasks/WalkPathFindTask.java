package net.itsred_v2.plaier.tasks;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.AsyncPathFinderWrapper;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinderExitStatus;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.ExplorerWalkPathFinder;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.WalkPathFinder;
import net.itsred_v2.plaier.rendering.PolylineRenderer;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.tasks.pathProcessing.PathProcessorResult;
import net.itsred_v2.plaier.tasks.pathProcessing.WalkPathProcessor;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;

public class WalkPathFindTask implements Task {

    private final BlockPos goal;
    private PolylineRenderer pathRenderer;
    private WalkPathFinder pathFinder;
    private WalkPathProcessor pathProcessor;
    private TaskState state = TaskState.READY;
    private BlockPos lastPathfindingStart;

    public WalkPathFindTask(BlockPos goal) {
        this.goal = goal;
    }

    @Override
    public void start() {
        if (state != TaskState.READY) throw new RuntimeException("Task started twice.");
        state = TaskState.RUNNING;

        pathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 0, 255, 255));
        pathRenderer.enable();

        startPathFinding();
    }

    @Override
    public void terminate() {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

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

    private void startPathFinding() {
        BlockPos start = PlaierClient.getPlayer().getBlockPos();
        this.lastPathfindingStart = start;
        pathFinder = new ExplorerWalkPathFinder(PlaierClient.getWorld(), start, goal);

        Messenger.send("Pathfinding...");
        new AsyncPathFinderWrapper(pathFinder, this::onPathFinderDone);
    }

    private void onPathFinderDone(PathFinderExitStatus result) {
        switch (result) {
            case FOUND -> {
                setRenderedPath(pathFinder.traceCurrentPathNodes());
                Messenger.send("Path found in %d ms.", pathFinder.getCalculationTime());
                startPathProcessor();
            }
            case INVALID_START -> {
                Messenger.send("§cError: §fInaccessible starting position.");
                terminate();
            }
            case INVALID_GOAL -> {
                Messenger.send("§cError: §fInaccessible goal position.");
                terminate();
            }
            case REACHED_ITERATION_LIMIT -> {
                Messenger.send("§cReached iteration limit: could not find a path to the goal. It may be unreachable or too far away.");
                terminate();
            }
            case TRAPPED -> {
                Messenger.send("§cThe goal is unreachable. The pathfinder ran out of paths to explore.");
                terminate();
            }
            case UNHANDLED_ERROR -> terminate();
            default -> {}
        }
    }

    private void setRenderedPath(List<Node> path) {
        pathRenderer.vertices = path.stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
    }

    private void startPathProcessor() {
        pathProcessor = new WalkPathProcessor(pathFinder.getPathValidator(), pathFinder.traceCurrentPathNodes(),
                this::onPathProcessorDone, this::onPathProcessorAdvance);
        pathProcessor.start();
    }

    private void onPathProcessorDone(PathProcessorResult result) {
        switch (result) {
            case ARRIVED -> {
                Messenger.send("§aSuccessfully arrived at %d %d %d. Releasing controls.", goal.getX(), goal.getY(), goal.getZ());
                terminate();
            }
            case OFF_PATH -> {
                Messenger.send("§6Player is off path: Restarting task...");
                pathProcessor = null;
                startPathFinding();
            }
            case INVALID_PATH -> {
                Messenger.send("§6The path became invalid: Restarting task...");
                pathProcessor = null;
                startPathFinding();
            }
            default -> {}
        }
    }

    private void onPathProcessorAdvance(BlockPos currentPos) {
        if (!currentPos.isWithinDistance(this.lastPathfindingStart, 32)) {
            pathProcessor.terminate();
            pathProcessor = null;
            startPathFinding();
        }
    }

}
