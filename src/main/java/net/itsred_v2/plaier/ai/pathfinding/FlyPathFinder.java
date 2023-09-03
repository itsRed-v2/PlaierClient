package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.List;

import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class FlyPathFinder extends PathFinder {

    private final ClientWorld world;
    private final BlockHelper blockHelper;

    public FlyPathFinder(BlockPos start, BlockPos goal, Session session) {
        super(start, goal);
        this.world = session.getWorld();
        this.blockHelper = session.getBlockHelper();
    }

    @Override
    public int calculateHcost(Node node) {
        return node.getPos().getManhattanDistance(this.goal);
    }

    @Override
    public int calculateGcost(Node node) {
        Node parent = node.getParent();
//        return parent == null ? 0 : parent.getGcost() + node.getPos().getManhattanDistance(parent.getPos());
        return parent == null ? 0 : parent.getGcost() + 1;
    }

    @Override
    public BlockPos[] getValidNeighbors(Node node) {

        return getNeighbors(node.getPos()).stream().filter(this::isPassable).toArray(BlockPos[]::new);
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        neighbors.add(pos.up());
        neighbors.add(pos.down());
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        return neighbors;
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
