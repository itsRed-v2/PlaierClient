package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class StopTaskCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        boolean success = PlaierClient.getTaskManager().stopTask();
        if (success) {
            Messenger.chat("Stopped current task.");
        } else {
            Messenger.chat("No task is running.");
        }
    }

    @Override
    public List<String> getHelp() {
        return List.of("Stops the current task.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":stoptask");
    }

    @Override
    public List<String> getNames() {
        return List.of("stoptask", "stop", "st");
    }

}
