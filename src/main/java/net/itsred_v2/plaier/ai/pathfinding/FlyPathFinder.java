package net.itsred_v2.plaier.ai.pathfinding;

import java.util.List;
import java.util.stream.Stream;

import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FlyPathFinder extends PathFinder {

    private final ClientWorld world;
    private final BlockHelper blockHelper;

    public FlyPathFinder(BlockPos start, BlockPos goal, Session session) {
        super(start, goal);
        this.world = session.getWorld();
        this.blockHelper = session.getBlockHelper();
    }

    @Override
    public int calculateGcost(BlockPos pos, @Nullable Node parent) {
        if (parent == null) return 0;
        return parent.getGcost() + 1;
    }

    @Override
    public int calculateHcost(BlockPos pos) {
        return pos.getManhattanDistance(this.goal);
    }

    @Override
    public List<BlockPos> getValidNeighbors(Node node) {
        BlockPos pos = node.getPos();
        return Stream.of(pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down())
                .filter((this::isPassable))
                .toList();
    }

    @Override
    public boolean isPassable(BlockPos pos) {
        return isBlockTraversable(pos) && isBlockTraversable(pos.up());
    }

    private boolean isBlockTraversable(BlockPos pos) {
        BlockState state = blockHelper.getBlockState(pos);
        if (state == null)
            return false;

        VoxelShape shape = state.getCollisionShape(world, pos);
        return shape.isEmpty();
    }

}
