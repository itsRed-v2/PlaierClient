package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import java.util.Set;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class ExplorerWalkPathFinder extends WalkPathFinder {

    public ExplorerWalkPathFinder(ClientWorld world, BlockPos start, BlockPos goal) {
        super(world, start, goal);
    }

    @Override
    public boolean isGoalReached(BlockPos currentPos) {
        if (this.goal.equals(currentPos))
            return true;

        // If the pathfinder encounters the border of the loaded world, we consider it done
        Set<BlockPos> neighbors = Set.of(currentPos.east(), currentPos.west(), currentPos.south(), currentPos.north());
        for (BlockPos pos : neighbors) {
            if (this.blockHelper.isUnloaded(pos))
                return true;
        }

        return false;
    }

    @Override
    public boolean isGoalValid() {
        if (this.blockHelper.isUnloaded(goal))
            return true;
        return canRestAt(goal);
    }

}
