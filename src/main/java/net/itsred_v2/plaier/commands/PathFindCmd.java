package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.tasks.WalkPathFindTask;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.util.math.BlockPos;

public class PathFindCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        Session session = PlaierClient.getCurrentSession();
        Messenger messenger = session.getMessenger();

        if (args.size() != 3) {
            messenger.send("§cInvalid syntax.");
            return;
        }

        int posX, posY, posZ;
        try {
            posX = Integer.parseInt(args.get(0));
            posY = Integer.parseInt(args.get(1));
            posZ = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            messenger.send("§cUnable to parse coordinates.");
            return;
        }

        BlockPos goal = new BlockPos(posX, posY, posZ);
        Task task = new WalkPathFindTask(goal);
        boolean success = session.getTaskManager().startTask(task);

        if (!success) {
            messenger.send("§cAnother task is already running.");
        }
    }

}
