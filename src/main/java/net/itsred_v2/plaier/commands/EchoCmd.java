package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.ChatUtils;

public class EchoCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        String message = String.join(" ", args);
        ChatUtils.sendMessage(message);
    }

}
