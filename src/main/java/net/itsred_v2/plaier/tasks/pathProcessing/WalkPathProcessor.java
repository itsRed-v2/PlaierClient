package net.itsred_v2.plaier.tasks.pathProcessing;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskState;
import net.itsred_v2.plaier.utils.control.MovementUtils;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WalkPathProcessor implements Task, UpdateListener {

    private TaskState state = TaskState.READY;
    private final List<Node> path;
    private int currentPathIndex = 0;

    public WalkPathProcessor(List<Node> path) {
        this.path = path;
    }

    @Override
    public void start() {
        if (state != TaskState.READY) throw new RuntimeException("Task started twice.");
        state = TaskState.RUNNING;

        PlaierClient.getEventManager().add(UpdateListener.class, this);
    }

    @Override
    public void terminate() {
        if (state != TaskState.RUNNING) return;
        state = TaskState.DONE;

        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        MovementUtils.lockControls(); // resetting controls
    }

    @Override
    public boolean isDone() {
        return state == TaskState.DONE;
    }

    @Override
    public void onUpdate() {
        if (state == TaskState.DONE) return;

        BlockPos nextPos = path.get(currentPathIndex).getPos();

        if (isPlayerInBlocks(nextPos)) {
            currentPathIndex++;
            if (currentPathIndex >= path.size()) {
                terminate();
                return;
            }
            nextPos = path.get(currentPathIndex).getPos();
        }

        Session session = PlaierClient.getCurrentSession();
        RotationHelper rotationHelper = session.getRotationHelper();
        BlockPos playerPos = session.getPlayer().getBlockPos();

        MovementUtils.lockControls();

        // Walk towards the block
        if (!isPlayerAboveBlock(nextPos)) {
            rotationHelper.facePosHorizontally(Vec3d.ofCenter(nextPos));
            MovementUtils.forward(true);
        }

        // If the block is 1 above the player, jump
        if (playerPos.getY() + 1 == nextPos.getY()) {
            MovementUtils.setJumping(true);
        }
    }

    private boolean isPlayerInBlocks(BlockPos pos) {
        Box playerBox = PlaierClient.getCurrentSession().getPlayer().getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                && playerBox.maxY <= pos.getY() + 2
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

    private boolean isPlayerAboveBlock(BlockPos pos) {
        Box playerBox = PlaierClient.getCurrentSession().getPlayer().getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

}
