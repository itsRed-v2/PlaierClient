package net.itsred_v2.plaier.tasks;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.AsyncPathFinderWrapper;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.ai.pathfinding.PathFinderOutput;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.ExplorerWalkPathFinder;
import net.itsred_v2.plaier.events.GameModeChangeListener;
import net.itsred_v2.plaier.events.PlayerDeathListener;
import net.itsred_v2.plaier.rendering.world.PolylineRenderer;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.tasks.pathProcessing.PathProcessorResult;
import net.itsred_v2.plaier.tasks.pathProcessing.WalkPathProcessor;
import net.itsred_v2.plaier.utils.control.PlayerController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;

public class WalkPathFindTask extends Task implements PlayerDeathListener, GameModeChangeListener {

    private final BlockPos goal;
    private PolylineRenderer bluePathRenderer;
    private PolylineRenderer redPathRenderer;
    private AsyncPathFinderWrapper pathFinderWrapper;
    private List<Node> path;
    private WalkPathProcessor pathProcessor;
    private PlayerController playerController;
    private TaskState state = TaskState.READY;
    private BlockPos lastPathfindingStart;

    public WalkPathFindTask(BlockPos goal) {
        this.goal = goal;
    }

    @Override
    public void start() {
        if (state != TaskState.READY) throw new RuntimeException("Task started twice.");
        state = TaskState.RUNNING;

        if (PlaierClient.MC.getGameMode() == GameMode.SPECTATOR) {
            this.output.fail("You cannot do this in spectator mode.");
            terminate();
            return;
        }

        bluePathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 0, 255, 255));
        bluePathRenderer.enable();
        redPathRenderer = new PolylineRenderer(ColorHelper.Argb.getArgb(255, 255, 0, 0));
        redPathRenderer.enable();

        playerController = new PlayerController();
        playerController.enable();

        PlaierClient.getEventManager().add(PlayerDeathListener.class, this);
        PlaierClient.getEventManager().add(GameModeChangeListener.class, this);

        this.output.chatInfo("Started pathfinding task.");
        startPathFinding();
    }

    @Override
    public void terminate() {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

        if (bluePathRenderer != null) bluePathRenderer.disable();
        if (redPathRenderer != null) redPathRenderer.disable();
        if (playerController != null) playerController.disable();

        if (pathFinderWrapper != null) pathFinderWrapper.cancel();
        if (pathProcessor != null) pathProcessor.terminate();

        PlaierClient.getEventManager().remove(PlayerDeathListener.class, this);
        PlaierClient.getEventManager().remove(GameModeChangeListener.class, this);
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
        this.output.info(isUpdate ? "Updating path..." : "Pathfinding...");

        BlockPos start = isUpdate ? path.get(updateIndex).getPos() : PlaierClient.getPlayer().getBlockPos();
        lastPathfindingStart = start;
        PathFinder pathFinder = new ExplorerWalkPathFinder(PlaierClient.MC.getWorldView(), start, goal);

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
                    this.output.info("Path updated in %d ms.".formatted(output.calculationTime()));
                } else {
                    path = output.path();
                    startPathProcessor(output.pathValidator(), path);
                    this.output.info("Path found in %d ms.".formatted(output.calculationTime()));
                }
                renderPath();
            }
            case INVALID_START -> {
                this.output.fail("Inaccessible starting position.");
                terminate();
            }
            case INVALID_GOAL -> {
                this.output.fail("Inaccessible goal position.");
                terminate();
            }
            case REACHED_ITERATION_LIMIT -> {
                this.output.fail("Reached iteration limit: could not find a path to the goal. It may be unreachable or too far away.");
                terminate();
            }
            case TRAPPED -> {
                this.output.fail("The goal is unreachable. The pathfinder ran out of paths to explore.");
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
        // The blue path represents the path that is not subject to change
        bluePathRenderer.vertices = path.subList(0, updateIndex + 1)
                .stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
        // The red path represents the path that is getting updated and will be discarded soon.
        redPathRenderer.vertices = path.subList(updateIndex, path.size())
                .stream()
                .map(node -> node.getPos().toCenterPos())
                .toList();
        redPathRenderer.enable();
    }

    private void startPathProcessor(PathFinder.PathValidator pathValidator, List<Node> path) {
        if (pathProcessor != null)
            pathProcessor.terminate();
        pathProcessor = new WalkPathProcessor(pathValidator, path, this::onPathProcessorDone, this::onPathProcessorAdvance, this.playerController);
        pathProcessor.start();
    }

    private void onPathProcessorDone(PathProcessorResult result) {
        switch (result) {
            case ARRIVED -> {
                this.output.success("Arrived at %d %d %d.".formatted(goal.getX(), goal.getY(), goal.getZ()));
                terminate();
            }
            case OFF_PATH -> {
                this.output.info("§6Player is off path: Reprocessing path...");
                pathProcessor = null;
                startPathFinding();
            }
            case INVALID_PATH -> {
                this.output.info("§6The path became invalid: Reprocessing path...");
                pathProcessor = null;
                startPathFinding();
            }
            default -> {}
        }
    }

    private void onPathProcessorAdvance(int currentIndex, BlockPos currentPos) {
        if (!currentPos.isWithinDistance(this.lastPathfindingStart, 32)) {
            int updateIndex = currentIndex + 20;
            // The game would crash from index ouf of bounds if updateIndex were greater than the path length...
            // And if we're close to the end of the path, aka the destination, it is not so useful to update the path.
            if (updateIndex >= path.size())
                return;
            updatePath(updateIndex);
        }
    }

    @Override
    public void onPlayerDeath() {
        this.output.fail("Player died: aborting task. Sorry for any inconvenience caused by PlaierClient. Please " +
                "note that we are not responsible for any losses caused by the use of our mod.");
        terminate();
    }

    @Override
    public void onGameModeChange(GameModeChangeEvent event) {
        if (event.gameMode == GameMode.SPECTATOR) {
            this.output.fail("Detected spectator mode: aborting task. This task does not support spectator mode.");
            terminate();
        }
    }
}
