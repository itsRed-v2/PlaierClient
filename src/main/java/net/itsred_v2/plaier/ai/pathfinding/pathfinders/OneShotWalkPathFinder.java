package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class OneShotWalkPathFinder extends WalkPathFinder {

    public OneShotWalkPathFinder(ClientWorld world, BlockPos start, BlockPos goal) {
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
