package net.itsred_v2.plaier.tasks;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.UpdateListener;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.utils.control.MovementUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTask implements Task, UpdateListener {

    private final BlockPos goal;
    private boolean running = false;
    private boolean done = false;

    public WalkTask(BlockPos goal) {
        this.goal = goal;
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
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void onUpdate() {
        if (running) {
            process();
        }
    }

    private void process() {
        Session session = PlaierClient.getCurrentSession();
        ClientPlayerEntity player = session.getPlayer();

        if (player.getBlockPos().equals(goal)) {
            done = true;
            terminate();
            session.getMessenger().send("§aWalk task done.");
            return;
        }

        Vec3d goalBlockCenter = new Vec3d(goal.getX() + 0.5, goal.getY() + 0.5, goal.getZ() + 0.5);
        session.getRotationHelper().facePosHorizontally(goalBlockCenter);

        MovementUtils.lockControls();
        MovementUtils.forward(true);
    }

}
