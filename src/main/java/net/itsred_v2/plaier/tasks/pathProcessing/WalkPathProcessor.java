package net.itsred_v2.plaier.tasks.pathProcessing;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.utils.control.MovementUtils;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WalkPathProcessor implements Task, UpdateListener {

    private static final int MAX_TICKS_OFF_PATH = 20; // 1 second

    private TaskState state = TaskState.READY;
    private final List<BlockPos> path;
    private final Callback callback;
    private int currentPathIndex = 0;
    private int ticksOffPath = 0;

    public WalkPathProcessor(List<BlockPos> path, Callback callback) {
        this.path = path;
        this.callback = callback;
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

        this.callback.call(result);
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    @Override
    public void onUpdate() {
        if (state == TaskState.DONE) return;

        Session session = PlaierClient.getCurrentSession();
        RotationHelper rotationHelper = session.getRotationHelper();
        ClientPlayerEntity player = session.getPlayer();
        BlockPos playerPos = player.getBlockPos();

        BlockPos nextPos = path.get(currentPathIndex);
        // If the player is in the next position, advance by one.
        if (isPlayerInBlocks(player, nextPos)) {
            currentPathIndex++;
            if (currentPathIndex >= path.size()) {
                terminate(PathProcessorResult.ARRIVED);
                return;
            }
            nextPos = path.get(currentPathIndex);
        }

        MovementUtils.lockControls();

        // Walk towards the block
        if (!isPlayerAboveBlock(player, nextPos)) {
            rotationHelper.facePosHorizontally(Vec3d.ofCenter(nextPos));
            MovementUtils.forward(true);
        }

        // If the block is 1 above the player, jump
        if (playerPos.getY() + 1 == nextPos.getY()) {
            MovementUtils.setJumping(true);
        }

        // checking if the player is lost off the path
        checkOffPath(player);
    }

    private boolean isPlayerInBlocks(ClientPlayerEntity player, BlockPos pos) {
        Box playerBox = player.getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
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
        return path.get(currentPathIndex).equals(playerPos);
    }

    public interface Callback {
        void call(PathProcessorResult result);
    }
}
