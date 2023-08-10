package net.itsred_v2.plaier.ai.pathfinding;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class FlyPathFinder extends PathFinder {

    private final ClientWorld world;

    public FlyPathFinder(BlockPos start, BlockPos goal, ClientWorld world) {
        super(start, goal);
        this.world = world;
    }

    @Override
    public int calculateHcost(Node node) {
        return node.getPos().getManhattanDistance(this.goal);
    }

    @Override
    public int calculateGcost(Node node) {
        Node parent = node.getParent();
        return parent == null ? 0 : parent.getGcost() + node.getPos().getManhattanDistance(parent.getPos());
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

    private boolean isPassable(BlockPos pos) {
        return isBlockTraversable(pos) && isBlockTraversable(pos.up());
    }

    private boolean isBlockTraversable(BlockPos pos) {
        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
        return shape.isEmpty();
    }

}
