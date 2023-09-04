package net.itsred_v2.plaier.ai.pathfinding;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Node {

    private final BlockPos pos;
    private final @Nullable Node parent;
    private final int Hcost;
    private final int Gcost;

    public Node(@Nullable Node parent, BlockPos pos, int Gcost, int Hcost) {
        this.parent = parent;
        this.pos = pos;
        this.Gcost = Gcost;
        this.Hcost = Hcost;
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

}
