package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.task.Task;
import net.itsred_v2.plaier.tasks.WalkPathFindTask;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PathFindCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        if (args.size() != 3) {
            this.sendSyntaxErrorMessage();
            return;
        }

        int posX, posY, posZ;
        try {
            posX = Integer.parseInt(args.get(0));
            posY = Integer.parseInt(args.get(1));
            posZ = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            Messenger.chat("§cUnable to parse coordinates.");
            return;
        }

        BlockPos goal = new BlockPos(posX, posY, posZ);
        Task task = new WalkPathFindTask(goal);
        boolean success = PlaierClient.getTaskManager().startTask(task);

        if (!success) {
            Messenger.chat("§cAnother task is already running.");
        }
    }

    @Override
    public List<String> getHelp() {
        return List.of("Tries to find a path to the given destination and if one is found, walks to it.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":path <x> <y> <z>");
    }

    @Override
    public List<String> getNames() {
        return List.of("path");
    }

}
