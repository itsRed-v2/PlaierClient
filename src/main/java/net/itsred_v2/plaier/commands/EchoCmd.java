package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class EchoCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        String message = String.join(" ", args);
        Messenger.chat(message);
    }

    @Override
    public List<String> getHelp() {
        return List.of("Echoes back to you what you tell it.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":echo <message>");
    }

    @Override
    public List<String> getNames() {
        return List.of("echo");
    }

}
