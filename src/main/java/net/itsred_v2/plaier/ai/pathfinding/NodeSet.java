package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class NodeSet {

    private final Set<Node> nodeSet = new HashSet<>();

    public void remove(Node node) {
        nodeSet.remove(node);
    }

    public int size() {
        return nodeSet.size();
    }

    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public void addIfBetter(Node newNode) {
        Node previousNode = getNodeAt(newNode.getPos());

        if (previousNode == null) {
            nodeSet.add(newNode);
        }
        else if (newNode.getFcost() < previousNode.getFcost()) {
            nodeSet.remove(previousNode);
            nodeSet.add(newNode);
        }
    }

    // TODO: optimise
    private @Nullable Node getNodeAt(BlockPos pos) {
        for (Node node : nodeSet) {
            if (node.getPos().equals(pos)) {
                return node;
            }
        }
        return null;
    }

    // TODO: optimise
    public Node getBestNode() {
        List<Node> bestNodes = new ArrayList<>();
        for (Node n : nodeSet) {
            if (bestNodes.isEmpty()) {
                bestNodes.add(n);
            }
            else {
                double bestFcost = bestNodes.get(0).getFcost();
                double bestHcost = bestNodes.get(0).getHcost();

                if (n.getFcost() == bestFcost && n.getHcost() == bestHcost) {
                    bestNodes.add(n);
                }
                else if (n.getFcost() < bestFcost || (n.getFcost() == bestFcost && n.getHcost() < bestHcost)) {
                    bestNodes.clear();
                    bestNodes.add(n);
                }
            }
        }
        return bestNodes.get((int) Math.floor(Math.random() * bestNodes.size()));
    }

}
