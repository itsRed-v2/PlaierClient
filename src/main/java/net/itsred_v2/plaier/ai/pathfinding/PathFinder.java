package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PathFinder {

    private static final int MAX_ITERATIONS = 100000;

    private final NodeSet OPEN = new NodeSet();
    private final Set<BlockPos> CLOSED = new HashSet<>();

    private Node current;
    private boolean started = false;
    private boolean done = false;
    private PathFinderExitStatus exitStatus;
    private long calculationTime = -1;
    private boolean shouldStop = false;

    public final BlockPos start;
    public final BlockPos goal;

    public PathFinder(BlockPos start, BlockPos goal) {
        this.start = start;
        this.goal = goal;
    }

    public void start() {
        if (started) return;
        started = true;

        long startTime = new Date().getTime();

        this.exitStatus = process();
        done = true;

        long endTime = new Date().getTime();
        this.calculationTime = endTime - startTime;

        // Debug
        PlaierClient.LOGGER.info("OPEN set size: " + OPEN.size());
        PlaierClient.LOGGER.info("CLOSED set size: " + CLOSED.size());
        List<Node> path = traceCurrentPathNodes();
        PlaierClient.LOGGER.info("PATH size: " + path.size());
        PlaierClient.LOGGER.info("Calculation time: " + calculationTime + " ms");
    }

    private PathFinderExitStatus process() {
        if (!isStartValid()) {
            return PathFinderExitStatus.INVALID_START;
        }

        Node startNode = createStartingNode();
        OPEN.addIfBetter(startNode);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (shouldStop)
                return PathFinderExitStatus.STOPPED;

            // rechecking goal at each iteration in case terrain changes
            if (!isGoalValid())
                return PathFinderExitStatus.INVALID_GOAL;

            if (OPEN.isEmpty())
                return PathFinderExitStatus.TRAPPED;
            current = OPEN.getBestNode();
            OPEN.remove(current);
            CLOSED.add(current.getPos());

            if (isGoalReached(current.getPos())) {
                return PathFinderExitStatus.FOUND;
            }

            for (Node neighborNode : getValidNeighbors(current)) {
                if (CLOSED.contains(neighborNode.getPos())) continue;
                OPEN.addIfBetter(neighborNode);
            }
        }

        return PathFinderExitStatus.REACHED_ITERATION_LIMIT;
    }

    public abstract Node createStartingNode();

    public abstract List<Node> getValidNeighbors(Node parentNode);

    public abstract boolean isStartValid();

    public abstract boolean isGoalValid();

    public abstract boolean isGoalReached(BlockPos currentPos);

    public boolean isDone() {
        return done;
    }

    public @Nullable PathFinderExitStatus getExitStatus() {
        return exitStatus;
    }

    public long getCalculationTime() {
        return calculationTime;
    }

    public void stop() {
        this.shouldStop = true;
    }

    public List<Node> traceCurrentPathNodes() {
        List<Node> path = new ArrayList<>();
        Node currentNode = current;

        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path); // we need to reverse the path because we built it from end to start.
        return path;
    }

    public PathValidator getPathValidator() {
        return this::isPathValid;
    }

    public boolean isPathValid(List<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);

            if (!isConnectionValid(current, next))
                return false;
        }

        return true;
    }

    private boolean isConnectionValid(Node parent, Node child) {
        BlockPos followingPos = child.getPos();
        for (Node neighbor : getValidNeighbors(parent)) {
            if (followingPos.equals(neighbor.getPos()))
                return true;
        }
        return false;
    }

    public interface PathValidator {
        boolean verify(List<Node> path);
    }

}
