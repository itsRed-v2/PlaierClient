package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WalkPathFinder extends PathFinder {

    private static final int MAX_FALL_HEIGHT = 3;
    public static final double STRAIGHT_WEIGHT = 1;
    public static final double DIAGONAL_WEIGHT = 1.414;
    public static final double VERTICAL_WEIGHT = 0.1;

    private final ClientWorld world;
    private final BlockHelper blockHelper;

    public WalkPathFinder(BlockPos start, BlockPos goal, Session session) {
        super(start, goal);
        this.world = session.getWorld();
        this.blockHelper = session.getBlockHelper();
    }

    public double calculateHcost(BlockPos pos) {
        Vec3i diff = pos.subtract(this.goal);
        int dx = Math.abs(diff.getX());
        int dy = Math.abs(diff.getY());
        int dz = Math.abs(diff.getZ());

        double dxz;
        if (dx < dz)
            dxz = dx * DIAGONAL_WEIGHT + (dz - dx);
        else
            dxz = dz * DIAGONAL_WEIGHT + (dx - dz);

        return dxz + (dy * VERTICAL_WEIGHT);
    }

    @Override
    public Node createStartingNode() {
        return new Node(null, this.start, 0, calculateHcost(this.start));
    }

    @Override
    public List<Node> getValidNeighbors(Node parentNode) {
        BlockPos pos = parentNode.getPos();

        BlockPos N = pos.north();
        BlockPos S = pos.south();
        BlockPos E = pos.east();
        BlockPos W = pos.west();
        BlockPos NE = pos.north().east();
        BlockPos NW = pos.north().west();
        BlockPos SE = pos.south().east();
        BlockPos SW = pos.south().west();

        List<Node> neighborNodes = new ArrayList<>();
        neighborNodes.add(processMovementStraight(parentNode, N));
        neighborNodes.add(processMovementStraight(parentNode, S));
        neighborNodes.add(processMovementStraight(parentNode, E));
        neighborNodes.add(processMovementStraight(parentNode, W));
        neighborNodes.add(processMovementDiagonal(parentNode, NE, N, E));
        neighborNodes.add(processMovementDiagonal(parentNode, NW, N, W));
        neighborNodes.add(processMovementDiagonal(parentNode, SE, S, E));
        neighborNodes.add(processMovementDiagonal(parentNode, SW, S, W));

        neighborNodes.removeIf(Objects::isNull);

        return neighborNodes;
    }

    private @Nullable Node processMovementStraight(Node parent, BlockPos next) {
        double gcost = parent.getGcost() + STRAIGHT_WEIGHT;

        // Walking straight forward
        if (isAllowed(next)) {
            return new Node(parent, next, gcost, calculateHcost(next));
        }

        // Jumping 1 block up
        BlockPos up = next.up();
        if (isAllowed(up) && isTraversable(parent.getPos().up())) {
            gcost += VERTICAL_WEIGHT;
            return new Node(parent, up, gcost, calculateHcost(up));
        }

        // Falling forward
        for (int blocksFallen = 0; blocksFallen <= MAX_FALL_HEIGHT; blocksFallen++) {
            BlockPos current = next.down(blocksFallen);
            if (!isTraversable(current))
                break;
            if (isAllowed(current)) {
                gcost += VERTICAL_WEIGHT * blocksFallen;
                return new Node(parent, current, gcost, calculateHcost(current));
            }
        }

        return null;
    }

    private @Nullable Node processMovementDiagonal(Node parent, BlockPos next, BlockPos side1, BlockPos side2) {
        double gcost = parent.getGcost() + DIAGONAL_WEIGHT;

        // Walking diagonally forward
        if (isAllowed(next) && isTraversable(side1) && isTraversable(side2)) {
            return new Node(parent, next, gcost, calculateHcost(next));
        }

        // Jumping 1 block up
        BlockPos nextUp = next.up();
        if (isAllowed(nextUp)
                && isTraversable(parent.getPos().up())
                && isTraversable(side1.up())
                && isTraversable(side2.up())) {
            gcost += VERTICAL_WEIGHT;
            return new Node(parent, nextUp, gcost, calculateHcost(nextUp));
        }

        // Falling
        if (isTraversable(side1) && isTraversable(side2)) {
            for (int blocksFallen = 0; blocksFallen <= MAX_FALL_HEIGHT; blocksFallen++) {
                BlockPos current = next.down(blocksFallen);
                if (!isTraversable(current))
                    break;
                if (isAllowed(current)) {
                    gcost += VERTICAL_WEIGHT * blocksFallen;
                    return new Node(parent, current, gcost, calculateHcost(current));
                }
            }
        }

        return null;
    }

    @Override
    public boolean isAllowed(BlockPos pos) {
        return isTraversable(pos) && canWalkOn(pos.down());
    }

    private boolean isTraversable(BlockPos pos) {
        return isBlockTraversable(pos) && isBlockTraversable(pos.up());
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
