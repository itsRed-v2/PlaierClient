package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;

public class EchoCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        String message = String.join(" ", args);
        PlaierClient.getCurrentSession().getMessenger().send(message);
    }

}
