package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.session.Session;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.task.TaskLifeManager;
import net.itsred_v2.plaier.tasks.WalkTask;
import net.minecraft.util.math.BlockPos;

public class WalkCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        Session session = PlaierClient.getCurrentSession();
        TaskLifeManager taskManager = session.getTaskManager();

        if (args.size() != 3) {
            Messenger.send("§cInvalid syntax.");
            return;
        }

        int posX = Integer.parseInt(args.get(0));
        int posY = Integer.parseInt(args.get(1));
        int posZ = Integer.parseInt(args.get(2));

        BlockPos goal = new BlockPos(posX, posY, posZ);

        Task walkTask = new WalkTask(goal);
        boolean success = taskManager.startTask(walkTask);

        if (success) {
            Messenger.send("§aStarted walk task.");
        } else {
            Messenger.send("§cAnother task is already running.");
        }

    }

}
