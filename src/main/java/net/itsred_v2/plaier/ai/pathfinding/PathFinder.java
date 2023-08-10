package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PathFinder {

    private static final int MAX_ITERATIONS = 10000;

    private final TreeSet<Node> OPEN = new TreeSet<>((n1, n2) -> {
        if (n1.getFcost() > n2.getFcost()) return 1;
        else if (n1.getFcost() < n2.getFcost()) return -1;
        else return Integer.compare(n1.getHcost(), n2.getHcost());
    });

    @SuppressWarnings("FieldCanBeLocal")
    private final Set<BlockPos> CLOSED = new HashSet<>();

    public final BlockPos start;
    public final BlockPos goal;

    private boolean done = false;
    @Nullable
    private List<BlockPos> path;

    public PathFinder(BlockPos start, BlockPos goal) {
        this.start = start;
        this.goal = goal;
        process();
    }

    private void process() {
        Node startNode = new Node(null, start);
        OPEN.add(startNode);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            Node current = OPEN.pollFirst(); // get the best opened node
            // TODO: handle if OPEN is empty
            CLOSED.add(current.getPos());

            if (current.getPos().equals(goal)) {
                generatePath(current);
                done = true;
                break;
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
                        // changing the parent is equivalent to replacing the node
                        previousNode.setParent(current);
                        // after changing the parent we need to recalculate Fcost
                        computeFcost(previousNode);
                    }
                }
            }
        }
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

    public abstract int calculateHcost(Node node);

    public abstract int calculateGcost(Node node);

    public abstract BlockPos[] getValidNeighbors(Node node);

    public boolean isDone() {
        return done;
    }

    public @Nullable List<BlockPos> getPath() {
        return path;
    }

    public void generatePath(Node finalNode) {
        List<BlockPos> path = new ArrayList<>();
        Node currentNode = finalNode;

        while (currentNode != null) {
            path.add(currentNode.getPos());
            currentNode = currentNode.getParent();
        }

        this.path = path;
    }
    
}
