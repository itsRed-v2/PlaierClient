package net.itsred_v2.plaier.tasks;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.AsyncPathFinderWrapper;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.ai.pathfinding.PathFinderOutput;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.ExplorerWalkPathFinder;
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
    private PolylineRenderer bluePathRenderer;
    private PolylineRenderer redPathRenderer;
    private AsyncPathFinderWrapper pathFinderWrapper;
    private List<Node> path;
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

        bluePathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 0, 255, 255));
        bluePathRenderer.enable();
        redPathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 255, 0, 0));
        redPathRenderer.enable();

        startPathFinding();
    }

    @Override
    public void terminate() {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

        bluePathRenderer.disable();
        redPathRenderer.disable();

        if (pathFinderWrapper != null)
            pathFinderWrapper.cancel();

        if (pathProcessor != null)
            pathProcessor.terminate();
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    private void startPathFinding() {
        pathFind(false, -1);
    }

    private void updatePath(int updateIndex) {
        pathFind(true, updateIndex);
    }

    private void pathFind(boolean isUpdate, int updateIndex) {
        if (isUpdate) {
            renderPathUpdating(updateIndex);
        }
        Messenger.send(isUpdate ? "Updating path..." : "Pathfinding...");

        BlockPos start = isUpdate ? path.get(updateIndex).getPos() : PlaierClient.getPlayer().getBlockPos();
        lastPathfindingStart = start;
        PathFinder pathFinder = new ExplorerWalkPathFinder(PlaierClient.getWorld(), start, goal);

        if (pathFinderWrapper != null)
            pathFinderWrapper.cancel();

        pathFinderWrapper = new AsyncPathFinderWrapper(pathFinder, output -> onPathFinderDone(isUpdate, updateIndex, output));
    }

    private void onPathFinderDone(boolean isUpdate, int updateIndex, PathFinderOutput output) {
        switch (output.exitStatus()) {
            case FOUND -> {
                if (isUpdate) {
                    path.subList(updateIndex, path.size()).clear(); // removes all nodes after updateIndex (included)
                    path.addAll(output.path()); // appends all nodes from the newly processed path
                    pathProcessor.replacePath(path);
                    Messenger.send("Path updated in %d ms.", output.calculationTime());
                } else {
                    path = output.path();
                    startPathProcessor(output.pathValidator(), path);
                    Messenger.send("Path found in %d ms.", output.calculationTime());
                }
                renderPath();
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

    private void renderPath() {
        bluePathRenderer.vertices = path.stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
        redPathRenderer.disable();
    }

    private void renderPathUpdating(int updateIndex) {
        bluePathRenderer.vertices = path.subList(0, updateIndex + 1)
                .stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
        redPathRenderer.vertices = path.subList(updateIndex, path.size())
                .stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
        redPathRenderer.enable();
    }

    private void startPathProcessor(PathFinder.PathValidator pathValidator, List<Node> path) {
        if (pathProcessor != null)
            pathProcessor.terminate();
        pathProcessor = new WalkPathProcessor(pathValidator, path, this::onPathProcessorDone, this::onPathProcessorAdvance);
        pathProcessor.start();
    }

    private void onPathProcessorDone(PathProcessorResult result) {
        switch (result) {
            case ARRIVED -> {
                Messenger.send("§aSuccessfully arrived at %d %d %d. Releasing controls.", goal.getX(), goal.getY(), goal.getZ());
                terminate();
            }
            case OFF_PATH -> {
                Messenger.send("§6Player is off path: Recalculating path...");
                pathProcessor = null;
                startPathFinding();
            }
            case INVALID_PATH -> {
                Messenger.send("§6The path became invalid: Recalculating path...");
                pathProcessor = null;
                startPathFinding();
            }
            default -> {}
        }
    }

    private void onPathProcessorAdvance(int currentIndex, BlockPos currentPos) {
        if (!currentPos.isWithinDistance(this.lastPathfindingStart, 32)) {
            int updateIndex = currentIndex + 5;
            updatePath(updateIndex);
        }
    }

}
