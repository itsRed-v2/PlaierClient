package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import java.util.List;
import java.util.stream.Stream;

import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
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
        return Stream.of(pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down())
                .filter((this::isAllowed))
                .map(newPos -> new Node(parentNode, newPos, parentNode.getGcost() + 1, calculateHcost(newPos)))
                .toList();
    }

    @Override
    public boolean isAllowed(BlockPos pos) {
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
