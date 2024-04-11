package net.itsred_v2.plaier.ai.pathfinding.pathfinders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.utils.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WalkPathFinder extends PathFinder {

    private static final Set<Block> DANGEROUS_BLOCKS = Set.of(Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.LAVA, Blocks.POWDER_SNOW);
    private static final int MAX_FALL_HEIGHT = 3;
    public static final double STRAIGHT_WEIGHT = 1;
    public static final double DIAGONAL_WEIGHT = 1.414;
    public static final double VERTICAL_WEIGHT = 0.2;

    private final ClientWorld world;
    private final BlockHelper blockHelper;

    public WalkPathFinder(ClientWorld world, BlockPos start, BlockPos goal) {
        super(start, goal);
        this.world = world;
        this.blockHelper = new BlockHelper(world);
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

        if (canWalkOn(pos.down())) {
            return getNeighborsOfRestingNode(parentNode);
        } else if (isBlockTraversable(pos.down())) {
            return getNeighborsOfFlyingNode(parentNode);
        } else {
            PlaierClient.LOGGER.info("Found unsupported block wile pathfinding: {}", pos.down());
            return new ArrayList<>();
        }
    }

    private List<Node> getNeighborsOfFlyingNode(Node parent) {
        double gcost = parent.getGcost();

        BlockPos pos = parent.getPos();

        for (int blocksFallen = 0; blocksFallen <= MAX_FALL_HEIGHT; blocksFallen++) {
            BlockPos current = pos.down(blocksFallen);
            if (!isTraversable(current))
                break;
            if (canRestAt(current)) {
                gcost += VERTICAL_WEIGHT * blocksFallen;
                return List.of(new Node(parent, current, gcost, calculateHcost(current)));
            }
        }

        return List.of();
    }

    private List<Node> getNeighborsOfRestingNode(Node parentNode) {
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
        if (isTraversable(next)) {
            return new Node(parent, next, gcost, calculateHcost(next));
        }

        // Jumping 1 block up
        BlockPos nextUp = next.up();
        if (canRestAt(nextUp) && isTraversable(parent.getPos().up())) {
            gcost += VERTICAL_WEIGHT;
            return new Node(parent, nextUp, gcost, calculateHcost(nextUp));
        }

        return null;
    }

    private @Nullable Node processMovementDiagonal(Node parent, BlockPos next, BlockPos side1, BlockPos side2) {
        double gcost = parent.getGcost() + DIAGONAL_WEIGHT;

        // Walking diagonally forward
        if (isTraversable(next) && isTraversable(side1) && isTraversable(side2)) {
            return new Node(parent, next, gcost, calculateHcost(next));
        }

        // Jumping 1 block up
        BlockPos nextUp = next.up();
        if (canRestAt(nextUp)
                && isTraversable(parent.getPos().up())
                && isTraversable(side1.up())
                && isTraversable(side2.up())) {
            gcost += VERTICAL_WEIGHT;
            return new Node(parent, nextUp, gcost, calculateHcost(nextUp));
        }

        return null;
    }

    @Override
    public boolean isAllowed(BlockPos pos) {
        return isTraversable(pos);
    }

    public boolean canRestAt(BlockPos pos) {
        return isTraversable(pos) && canWalkOn(pos.down());
    }

    private boolean isTraversable(BlockPos pos) {
        return isBlockTraversable(pos) && isBlockTraversable(pos.up());
    }

    private boolean isBlockTraversable(BlockPos pos) {
        BlockState state = blockHelper.getBlockState(pos);
        if (state == null)
            return false;

        if (DANGEROUS_BLOCKS.contains(state.getBlock())) return false;

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
