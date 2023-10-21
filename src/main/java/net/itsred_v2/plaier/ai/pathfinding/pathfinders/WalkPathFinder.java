package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class WalkPathFinder extends PathFinder {

    private static final int MAX_FALL_HEIGHT = 3;

    private final ClientWorld world;
    private final BlockHelper blockHelper;

    public WalkPathFinder(BlockPos start, BlockPos goal, Session session) {
        super(start, goal);
        this.world = session.getWorld();
        this.blockHelper = session.getBlockHelper();
    }

    public double calculateHcost(BlockPos pos) {
        return pos.getManhattanDistance(this.goal);
    }

    @Override
    public Node createStartingNode() {
        return new Node(null, this.start, 0, calculateHcost(this.start));
    }

    @Override
    public List<Node> getValidNeighbors(Node parentNode) {
        BlockPos pos = parentNode.getPos();

        List<BlockPos> neighborsPositions = new ArrayList<>();
        for (BlockPos nextPos : Set.of(pos.north(), pos.south(), pos.east(), pos.west())) {
            BlockPos adjustedPos = adjustHeight(nextPos);
            if (adjustedPos != null)
                neighborsPositions.add(adjustedPos);
        }

        List<Node> neighbors = new ArrayList<>();
        for (BlockPos newPos : neighborsPositions) {
            Node neighbor = new Node(parentNode, newPos, parentNode.getGcost() + 1, calculateHcost(newPos));
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    private BlockPos adjustHeight(BlockPos pos) {
        if (isPassable(pos))
            return pos;

        if (isPassable(pos.up()))
            return pos.up();
        
        // FIXME: goes down even if there are blocks in the way
        for (int i = 1; i <= MAX_FALL_HEIGHT; i++) {
            if (isPassable(pos.down(i)))
                return pos.down(i);
        }
        return null;
    }

    @Override
    public boolean isPassable(BlockPos pos) {
        return isBlockTraversable(pos) && isBlockTraversable(pos.up()) && canWalkOn(pos.down());
    }

    private boolean isBlockTraversable(BlockPos pos) {
        BlockState state = blockHelper.getBlockState(pos);
        if (state == null)
            return false;

        VoxelShape shape = state.getCollisionShape(world, pos);
        return shape.isEmpty();
    }

    private boolean canWalkOn(BlockPos pos) {
        BlockState state = blockHelper.getBlockState(pos);
        if (state == null)
            return false;
        return state.isFullCube(world, pos);
    }

}
