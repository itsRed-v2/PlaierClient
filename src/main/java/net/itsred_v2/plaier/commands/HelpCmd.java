package net.itsred_v2.plaier.commands;

import joptsimple.internal.Strings;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.command.Commands;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.Collection;
import java.util.List;

public class HelpCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        if (args.isEmpty()) {
            listCommands();
        } else {
            String commandName = args.getFirst();
            Command cmd = Commands.getByName(commandName);
            if (cmd == null) {
                Messenger.chat("§cUnknown command: §6%s", commandName);
                Messenger.chat("§cType :help for a list of available commands.");
            } else {
                sendHelp(cmd);
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(List<String> args) {
        if (args.size() == 1) {
            return Commands.COMMAND_MAP.keySet();
        } else {
            return List.of();
        }
    }

    private void listCommands() {
        Messenger.chat("%d commands available:", Commands.COMMANDS.size());

        for (Command cmd : Commands.COMMANDS) {
            StringBuilder builder = new StringBuilder();
            builder.append("§7- §6");
            builder.append(cmd.getName());
            List<String> aliases = cmd.getAliases();
            if (!aliases.isEmpty()) {
                builder.append(" §7(alias ");
                builder.append(Strings.join(aliases, ", "));
                builder.append(")");
            }

            Messenger.chat(builder.toString());
        }
    }

    private void sendHelp(Command command) {
        Messenger.chat("§e--- §rHelp for command: §6%s §e---", command.getName());

        for (String helpLine : command.getHelp()) {
            Messenger.chat(helpLine);
        }

        if (!command.getAliases().isEmpty()) {
            Messenger.chat("§7Aliases: §e%s", Strings.join(command.getAliases(), "§7, §e"));
        }

        Messenger.chat("§7Use:");
        for (String useLine : command.getUse()) {
            Messenger.chat("§7- §6" + useLine);
        }
    }

    @Override
    public List<String> getHelp() {
        return List.of(
                "Gives help about commands.",
                "With no argument, lists out every command.",
                "If given a command name, shows information about that command."
        );
    }

    @Override
    public List<String> getUse() {
        return List.of(":help", ":help <command>");
    }

    @Override
    public List<String> getNames() {
        return List.of("help");
    }

}
