package net.itsred_v2.plaier.commands;

import java.util.List;
import java.util.Set;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.command.CommandProcessor;
import net.itsred_v2.plaier.utils.Messenger;

public class HelpCommand implements Command {

    @Override
    public void onCommand(List<String> args) {
        Set<String> commands = CommandProcessor.COMMAND_MAP.keySet();

        Messenger.send("%d commands available:", commands.size());
        for (String commandName : commands) {
            Messenger.send("§7- §6%s", commandName);
        }
    }

}
