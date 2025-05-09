package net.itsred_v2.plaier.tasks.pathProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.rendering.world.BoxRenderer;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.utils.control.PlayerController;
import net.itsred_v2.plaier.utils.control.RotationUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class WalkPathProcessor extends Task implements UpdateListener {

    private static final int MAX_TICKS_OFF_PATH = 20; // 1 second
    public static final BoxRenderer debugRenderer = new BoxRenderer(ColorHelper.fromFloats(0.8f, 1.0f, 0.0f, 0.0f));

    private TaskState state = TaskState.READY;
    private final PathFinder.PathValidator pathValidator;
    private List<Node> path;
    private final Consumer<PathProcessorResult> onArrive;
    private final BiConsumer<Integer, BlockPos> onAdvance;
    private final PlayerController controller;
    private int targetNodeIndex = 0;
    private int ticksOffPath = 0;

    public WalkPathProcessor(PathFinder.PathValidator pathValidator, List<Node> path,
                             Consumer<PathProcessorResult> onArrive, BiConsumer<Integer, BlockPos> onAdvance,
                             PlayerController controller) {
        this.pathValidator = pathValidator;
        this.path = new ArrayList<>(path);
        this.onArrive = onArrive;
        this.onAdvance = onAdvance;
        this.controller = controller;
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
        controller.keyboard.unpressAllKeys(); // making sure no keys stay pressed

        this.onArrive.accept(result);
        debugRenderer.box = null;
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    public void replacePath(List<Node> newPath) {
        this.path = new ArrayList<>(newPath);
    }

    @Override
    public void onUpdate() {
        if (this.state == TaskState.DONE) return;

        ClientPlayerEntity player = PlaierClient.getPlayer();

        checkOffPath(player); // checking if the player is lost off the path
        ensurePathValidity(); // making sure the path is still valid
        if (this.state == TaskState.DONE) return; // Task may be terminated by checkOffPath() and ensurePathValidity()

        advance(player); // Incrementing the targetNodeIndex if the player has reached the next position
        if (this.state == TaskState.DONE) return; // Task may be terminated by advance()

        // Applying the needed controls to get the player to the target node.
        control(player);

        // Update the debug renderer
        WalkPathProcessor.debugRenderer.box = getNodeBounds(path.get(targetNodeIndex).getPos());
    }

    private void advance(ClientPlayerEntity player) {
        // If the player is in one of the next positions, advance to this position.
        for (int index = targetNodeIndex; index < targetNodeIndex + 2 && index < path.size(); index++) {
            BlockPos nextPos = path.get(index).getPos();
            if (getNodeBounds(nextPos).contains(player.getPos())) {
                targetNodeIndex = index + 1;
                if (targetNodeIndex >= path.size()) {
                    terminate(PathProcessorResult.ARRIVED);
                } else {
                    // Trigger the onAdvance callback
                    this.onAdvance.accept(index, nextPos);
                }
                return;
            }
        }
    }

    private void control(ClientPlayerEntity player) {
        BlockPos nextPos = path.get(targetNodeIndex).getPos();

        controller.keyboard.unpressAllKeys();
        controller.disableFlying();

        // Walk towards the block
        if (!isPlayerAboveBlock(player, nextPos)) {
            RotationUtils.facePosHorizontally(Vec3d.ofCenter(nextPos));
            controller.keyboard.forwardKey(true);
        }

        boolean shouldJump = shouldJumpBefore(player, targetNodeIndex);
        boolean shouldJumpNext = shouldJumpBefore(player, targetNodeIndex + 1);
        controller.keyboard.jumpKey(shouldJump);
        controller.setSprinting(!shouldJump && !shouldJumpNext);
    }

    private boolean shouldJumpBefore(ClientPlayerEntity player, int nodeIndex) {
        nodeIndex = Math.min(path.size() - 1, nodeIndex);
        BlockPos pos = path.get(nodeIndex).getPos();
        return player.getBlockPos().getY() == pos.getY() - 1;
    }

    /**
     * Takes a {@link BlockPos} and returns the {@link Box} in which the player needs to be, to be considered to have reached this position.
     * @param pos The {@link BlockPos} we want to get the bounds of.
     * @return The {@link Box} in which the player needs to be, to be considered to have reached this position.
     */
    private Box getNodeBounds(BlockPos pos) {
        return new Box(pos).withMaxY(pos.getY() + 1.1);
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
