package net.itsred_v2.plaier.ai.pathfinding;

import java.util.Objects;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Node {

    private int Hcost;
    private int Gcost;
    private final BlockPos pos;
    private @Nullable Node parent;

    public Node(@Nullable Node parent, BlockPos pos) {
        this.parent = parent;
        this.pos = pos;
    }

    public int getFcost() {
        return Hcost + Gcost;
    }

    public int getHcost() {
        return Hcost;
    }

    public int getGcost() {
        return Gcost;
    }

    public @Nullable Node getParent() {
        return parent;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setHcost(int hcost) {
        this.Hcost = hcost;
    }

    public void setGcost(int gcost) {
        this.Gcost = gcost;
    }

    public void setParent(@Nullable Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return pos.equals(node.pos);
    }

    // TODO: check if adding hashCode makes things faster
    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

}
