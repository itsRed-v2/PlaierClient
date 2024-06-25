package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

public class StopTaskCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        boolean success = PlaierClient.getTaskManager().stopTask();
        if (success) {
            Messenger.chat("Stopped current task.");
        } else {
            Messenger.chat("No task is running.");
        }
    }

}
