package net.itsred_v2.plaier.tasks.pathProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.utils.control.MovementUtils;
import net.itsred_v2.plaier.utils.control.RotationUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WalkPathProcessor implements Task, UpdateListener {

    private static final int MAX_TICKS_OFF_PATH = 20; // 1 second

    private TaskState state = TaskState.READY;
    private final PathFinder.PathValidator pathValidator;
    private List<Node> path;
    private final Consumer<PathProcessorResult> onArrive;
    private final BiConsumer<Integer, BlockPos> onAdvance;
    private int targetNodeIndex = 0;
    private int ticksOffPath = 0;

    public WalkPathProcessor(PathFinder.PathValidator pathValidator, List<Node> path,
                             Consumer<PathProcessorResult> onArrive, BiConsumer<Integer, BlockPos> onAdvance) {
        this.pathValidator = pathValidator;
        this.path = new ArrayList<>(path);
        this.onArrive = onArrive;
        this.onAdvance = onAdvance;
    }

    @Override
    public void start() {
        if (state != TaskState.READY) throw new RuntimeException("Task started twice.");
        state = TaskState.RUNNING;

        PlaierClient.getEventManager().add(UpdateListener.class, this);
    }

    @Override
    public void terminate() {
        terminate(PathProcessorResult.TERMINATED);
    }

    private void terminate(PathProcessorResult result) {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        MovementUtils.lockControls(); // resetting controls

        this.onArrive.accept(result);
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    public void replacePath(List<Node> newPath) {
        for (int i = targetNodeIndex - 1; i < targetNodeIndex + 1; i++) {
            BlockPos oldPos = path.get(i).getPos();
            BlockPos newPos = newPath.get(i).getPos();
            if (!oldPos.equals(newPos)) {
                throw new IllegalArgumentException("Updated path changed around player's current position");
            }
        }

        this.path = new ArrayList<>(newPath);
    }

    @Override
    public void onUpdate() {
        if (state == TaskState.DONE) return;

        ClientPlayerEntity player = PlaierClient.getPlayer();

        // Incrementing the nextPathIndex if the player has reached the next position
        advance(player);
        // The task may be terminated in the advance() call, we need to take it into account
        if (this.state == TaskState.DONE) return;

        BlockPos playerPos = player.getBlockPos();
        BlockPos nextPos = path.get(targetNodeIndex).getPos();

        MovementUtils.lockControls();

        // Walk towards the block
        if (!isPlayerAboveBlock(player, nextPos)) {
            RotationUtils.facePosHorizontally(Vec3d.ofCenter(nextPos));
            MovementUtils.forward(true);
        }

        // If the block is 1 above the player, jump
        if (playerPos.getY() + 1 == nextPos.getY()) {
            MovementUtils.setJumping(true);
        }

        // checking if the player is lost off the path
        checkOffPath(player);
        // making sure the path is still valid
        ensurePathValidity();
    }

    private void advance(ClientPlayerEntity player) {
        // If the player is in one of the next positions, advance to this position.
        for (int index = targetNodeIndex; index < targetNodeIndex + 2 && index < path.size(); index++) {
            BlockPos nextPos = path.get(index).getPos();
            if (isPlayerInBlocks(player, nextPos)) {
                targetNodeIndex = index + 1;
                if (targetNodeIndex >= path.size()) {
                    terminate(PathProcessorResult.ARRIVED);
                } else {
                    this.onAdvance.accept(index, nextPos);
                }
                return;
            }
        }
    }

    private boolean isPlayerInBlocks(ClientPlayerEntity player, BlockPos pos) {
        Box playerBox = player.getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                // Notice the next line is missing in the method isPlayerAboveBlock()
                // It is the only difference between these two methods.
                && playerBox.maxY <= pos.getY() + 2
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

    private boolean isPlayerAboveBlock(ClientPlayerEntity player, BlockPos pos) {
        Box playerBox = player.getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

    private void checkOffPath(ClientPlayerEntity player) {
        if (notOffPath(player)) {
            ticksOffPath = 0;
            return;
        }

        ++ticksOffPath;
        if (ticksOffPath >= MAX_TICKS_OFF_PATH) {
            terminate(PathProcessorResult.OFF_PATH);
        }
    }

    private boolean notOffPath(ClientPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        for (int index = targetNodeIndex; index < targetNodeIndex + 2 && index < path.size(); index++) {
            BlockPos nextPos = path.get(index).getPos();
            if (nextPos.equals(playerPos))
                return true;
        }
        return false;
    }

    private void ensurePathValidity() {
        int startIndex = Math.max(targetNodeIndex - 1, 0);
        List<Node> pathAhead = path.subList(startIndex, path.size());
        if (!pathValidator.verify(pathAhead)) {
            terminate(PathProcessorResult.INVALID_PATH);
        }
    }

}
