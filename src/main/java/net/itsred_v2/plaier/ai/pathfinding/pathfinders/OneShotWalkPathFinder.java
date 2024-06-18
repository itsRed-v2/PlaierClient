package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class OneShotWalkPathFinder extends WalkPathFinder {

    public OneShotWalkPathFinder(WorldView world, BlockPos start, BlockPos goal) {
        super(world, start, goal);
    }

    @Override
    public boolean isGoalReached(BlockPos currentPos) {
        return this.goal.equals(currentPos);
    }

    @Override
    public boolean isGoalValid() {
        return canRestAt(this.goal);
    }

}
