package net.itsred_v2.plaier.ai.pathfinding;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Node {

    private final BlockPos pos;
    private final @Nullable Node parent;
    private final double Hcost;
    private final double Gcost;

    public Node(@Nullable Node parent, BlockPos pos, double Gcost, double Hcost) {
        this.parent = parent;
        this.pos = pos;
        this.Gcost = Gcost;
        this.Hcost = Hcost;
    }

    public double getFcost() {
        return Hcost + Gcost;
    }

    public double getHcost() {
        return Hcost;
    }

    public double getGcost() {
        return Gcost;
    }

    public @Nullable Node getParent() {
        return parent;
    }

    public BlockPos getPos() {
        return pos;
    }

}
