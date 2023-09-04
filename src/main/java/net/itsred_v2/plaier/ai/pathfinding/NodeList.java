package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class NodeList {

    private final List<Node> nodeList = new ArrayList<>();

    public void add(Node node) {
        nodeList.add(node);
    }

    public void remove(Node node) {
        nodeList.remove(node);
    }

    public int size() {
        return nodeList.size();
    }

    public boolean isEmpty() {
        return nodeList.isEmpty();
    }

    public @Nullable Node getNodeAt(BlockPos pos) {
        for (Node node : nodeList) {
            if (node.getPos().equals(pos)) {
                return node;
            }
        }
        return null;
    }

    public Node getBestNode() {
        List<Node> bestNodes = new ArrayList<>();
        for (Node n : nodeList) {
            if (bestNodes.isEmpty()) {
                bestNodes.add(n);
            }
            else {
                int bestFcost = bestNodes.get(0).getFcost();
                int bestHcost = bestNodes.get(0).getHcost();

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
