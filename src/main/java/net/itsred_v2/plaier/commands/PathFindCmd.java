package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.ai.pathfinding.PathFinder;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.FlyPathFinder;
import net.itsred_v2.plaier.ai.pathfinding.pathfinders.WalkPathFinder;
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

        if (args.size() != 4) {
            messenger.send("§cInvalid syntax.");
            return;
        }

        String pathFinderType = args.get(0);
        int posX = Integer.parseInt(args.get(1));
        int posY = Integer.parseInt(args.get(2));
        int posZ = Integer.parseInt(args.get(3));

        BlockPos goal = new BlockPos(posX, posY, posZ);
        BlockPos start = session.getPlayer().getBlockPos();

        PathFinder pathFinder = switch (pathFinderType) {
            case "fly" -> new FlyPathFinder(start, goal, session);
            case "walk" -> new WalkPathFinder(start, goal, session);
            default -> null;
        };

        if (pathFinder == null) {
            messenger.send("§cThe pathfinder type must be 'fly' or 'walk'");
            return;
        }

        Task task = new PathFindTask(pathFinder);
        boolean success = session.getTaskManager().startTask(task);

        if (!success) {
            messenger.send("§cAnother task is already running.");
        }
    }

}
