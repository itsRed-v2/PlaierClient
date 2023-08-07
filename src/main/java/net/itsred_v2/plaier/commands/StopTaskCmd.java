package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;
import net.itsred_v2.plaier.session.Session;

public class StopTaskCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        Session session = PlaierClient.getCurrentSession();
        Messenger messenger = session.getMessenger();

        boolean success = session.getTaskManager().stopTask();
        if (success) {
            messenger.send("Stopped current task.");
        } else {
            messenger.send("No task is running.");
        }
    }

}
