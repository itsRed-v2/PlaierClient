package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PathFinder {

    private static final int MAX_ITERATIONS = 10000;

    private final NodeSet OPEN = new NodeSet();
    private final Set<BlockPos> CLOSED = new HashSet<>();

    private Node current;
    private boolean started = false;
    private boolean done = false;
    private PathFinderResult result;
    private long calculationTime = -1;
    private boolean shouldStop = false;

    public final BlockPos start;
    public final BlockPos goal;

    @Nullable
    private List<BlockPos> path;

    public PathFinder(BlockPos start, BlockPos goal) {
        this.start = start;
        this.goal = goal;
    }

    public void start() {
        if (started) return;
        started = true;

        long startTime = new Date().getTime();

        this.result = process();
        done = true;

        long endTime = new Date().getTime();
        this.calculationTime = endTime - startTime;

        // Debug
        PlaierClient.LOGGER.info("OPEN set size: " + OPEN.size());
        PlaierClient.LOGGER.info("CLOSED set size: " + CLOSED.size());
        path = traceCurrentPath();
        PlaierClient.LOGGER.info("PATH size: " + path.size());
        PlaierClient.LOGGER.info("Calculation time: " + calculationTime + " ms");
    }

    private PathFinderResult process() {
        if (!isPassable(start)) {
            return PathFinderResult.INVALID_START;
        }

        Node startNode = createStartingNode();
        OPEN.addIfBetter(startNode);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (shouldStop)
                return PathFinderResult.STOPPED;

            // rechecking goal at each iteration in case terrain changes
            if (!isPassable(goal))
                return PathFinderResult.INVALID_GOAL;

            if (OPEN.isEmpty())
                return PathFinderResult.TRAPPED;
            current = OPEN.getBestNode();
            OPEN.remove(current);
            CLOSED.add(current.getPos());

            if (current.getPos().equals(goal)) {
                path = traceCurrentPath();
                return PathFinderResult.FOUND;
            }

            for (Node neighborNode : getValidNeighbors(current)) {
                if (CLOSED.contains(neighborNode.getPos())) continue;
                OPEN.addIfBetter(neighborNode);
            }
        }

        return PathFinderResult.REACHED_ITERATION_LIMIT;
    }

    public abstract Node createStartingNode();

    public abstract List<Node> getValidNeighbors(Node parentNode);

    public abstract boolean isPassable(BlockPos pos);

    public boolean isDone() {
        return done;
    }

    public @Nullable PathFinderResult getResult() {
        return result;
    }

    public long getCalculationTime() {
        return calculationTime;
    }

    public void stop() {
        this.shouldStop = true;
    }

    public List<BlockPos> traceCurrentPath() {
        List<BlockPos> path = new ArrayList<>();
        Node currentNode = current;

        while (currentNode != null) {
            path.add(currentNode.getPos());
            currentNode = currentNode.getParent();
        }

        return path;
    }

}
