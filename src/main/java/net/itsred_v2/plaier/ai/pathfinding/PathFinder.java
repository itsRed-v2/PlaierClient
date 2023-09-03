package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PathFinder {

    private static final int MAX_ITERATIONS = 10000;

    private final TreeSet<Node> OPEN = new TreeSet<>((n1, n2) -> {
        if (n1.getFcost() > n2.getFcost()) return 1;
        else if (n1.getFcost() < n2.getFcost()) return -1;
        else return Integer.compare(n1.getHcost(), n2.getHcost());
    });

    private final Set<BlockPos> CLOSED = new HashSet<>();
    private Node current;
    private boolean started = false;
    private boolean done = false;
    private boolean shouldStop = false;
    private PathFinderResult result;

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
        process();
    }

    private void process() {
        if (!isPassable(start)) {
            doneWithResult(PathFinderResult.INVALID_START);
            return;
        }

        Node startNode = new Node(null, start);
        OPEN.add(startNode);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (shouldStop) {
                doneWithResult(PathFinderResult.STOPPED);
                return;
            }

            // rechecking goal at each iteration in case terrain changes
            BlockHelper blockHelper = PlaierClient.getCurrentSession().getBlockHelper();
            if (blockHelper.isUnloaded(goal)) {
                doneWithResult(PathFinderResult.UNLOADED_GOAL);
                return;
            }
            if (!isPassable(goal)) {
                doneWithResult(PathFinderResult.INVALID_GOAL);
                return;
            }

//            current = OPEN.pollFirst(); // get the best opened node
            current = getBestOpenedNode();
            OPEN.remove(current);
            // TODO: handle if OPEN is empty
            CLOSED.add(current.getPos());

            if (current.getPos().equals(goal)) {
                path = generatePath(current);
                doneWithResult(PathFinderResult.FOUND);
                return;
            }

            for (BlockPos neighborPos : getValidNeighbors(current)) {
                if (CLOSED.contains(neighborPos)) continue;

                Node previousNode = getOpenNodeAt(neighborPos);
                Node newNode = new Node(current, neighborPos);
                computeFcost(newNode);

                if (previousNode == null) {
                    OPEN.add(newNode);
                } else {
                    int previousFcost = previousNode.getFcost();
                    int newFcost = newNode.getFcost();

                    if (newFcost < previousFcost) {
                        OPEN.remove(previousNode);
                        OPEN.add(newNode);
//                        // changing the parent is equivalent to replacing the node
//                        previousNode.setParent(current);
//                        // after changing the parent we need to recalculate Fcost
//                        computeFcost(previousNode);
                    }
                }
            }
        }

        doneWithResult(PathFinderResult.REACHED_ITERATION_LIMIT);
    }

    private @Nullable Node getOpenNodeAt(BlockPos pos) {
        for (Node node : OPEN) {
            if (node.getPos().equals(pos)) {
                return node;
            }
        }
        return null;
    }

    private void computeFcost(Node node) {
        node.setGcost(calculateGcost(node));
        node.setHcost(calculateHcost(node));
    }

    private void doneWithResult(PathFinderResult result) {
        done = true;
        this.result = result;

        PlaierClient.LOGGER.info("OPEN set size: " + OPEN.size());
        PlaierClient.LOGGER.info("CLOSED set size: " + CLOSED.size());
        path = generateUnfinishedPath();
        PlaierClient.LOGGER.info("PATH size: " + path.size());
    }

    private Node getBestOpenedNode() {
        Node bestNode = OPEN.last();
        for (Node n : OPEN) {
            if (n.getFcost() < bestNode.getFcost() || (n.getFcost() == bestNode.getFcost() && n.getHcost() < bestNode.getHcost())) {
                bestNode = n;
            }
        }
        return bestNode;
    }

    public abstract int calculateHcost(Node node);

    public abstract int calculateGcost(Node node);

    public abstract BlockPos[] getValidNeighbors(Node node);

    public abstract boolean isPassable(BlockPos pos);

    public boolean isDone() {
        return done;
    }

    public @Nullable PathFinderResult getResult() {
        return result;
    }

    public void stop() {
        this.shouldStop = true;
    }

    public @Nullable List<BlockPos> getPath() {
        return path;
    }

    private List<BlockPos> generatePath(Node finalNode) {
        List<BlockPos> path = new ArrayList<>();
        Node currentNode = finalNode;

        while (currentNode != null) {
            path.add(currentNode.getPos());
            currentNode = currentNode.getParent();
        }

        return path;
    }

    public List<BlockPos> generateUnfinishedPath() {
        return generatePath(current);
    }

}
