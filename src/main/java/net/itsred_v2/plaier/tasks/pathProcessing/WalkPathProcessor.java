package net.itsred_v2.plaier.tasks.pathProcessing;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.Node;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.utils.control.MovementUtils;
import net.itsred_v2.plaier.utils.control.RotationHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WalkPathProcessor implements Task, UpdateListener {

    private boolean running = false;
    private boolean done = false;

    private final List<Node> path;
    private int index;

    public WalkPathProcessor(List<Node> path) {
        this.path = path;
        this.index = path.size() - 1; // we start at the end of the path because it is reversed
    }

    @Override
    public void start() {
        if (running) throw new RuntimeException("Task started twice.");
        running = true;
        PlaierClient.getEventManager().add(UpdateListener.class, this);
    }

    @Override
    public void terminate() {
        if (!running) return;
        running = false;

        PlaierClient.getEventManager().remove(UpdateListener.class, this);
        MovementUtils.lockControls(); // resetting controls

        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void onUpdate() {
        BlockPos nextPos = path.get(index).getPos();

        if (isPlayerInBlocks(nextPos)) {
            index--;
            if (index < 0) {
                terminate();
                return;
            }
        }

        nextPos = path.get(index).getPos();

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

    public boolean isPlayerInBlocks(BlockPos pos) {
        Box playerBox = PlaierClient.getCurrentSession().getPlayer().getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                && playerBox.maxY <= pos.getY() + 2
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

    public boolean isPlayerAboveBlock(BlockPos pos) {
        Box playerBox = PlaierClient.getCurrentSession().getPlayer().getBoundingBox();
        return playerBox.minX >= pos.getX()
                && playerBox.maxX <= pos.getX() + 1
                && playerBox.minY >= pos.getY()
                && playerBox.minZ >= pos.getZ()
                && playerBox.maxZ <= pos.getZ() + 1;
    }

}
