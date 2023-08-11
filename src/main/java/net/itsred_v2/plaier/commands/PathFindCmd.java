package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.tasks.PathFindTask;
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

        int posX = Integer.parseInt(args.get(0));
        int posY = Integer.parseInt(args.get(1));
        int posZ = Integer.parseInt(args.get(2));

        BlockPos goal = new BlockPos(posX, posY, posZ);

        Task task = new PathFindTask(goal);
        boolean success = session.getTaskManager().startTask(task);

        if (!success) {
            messenger.send("§cAnother task is already running.");
        }
    }

}
