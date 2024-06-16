package net.itsred_v2.plaier.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.itsred_v2.plaier.commands.EchoCmd;
import net.itsred_v2.plaier.commands.HelpCommand;
import net.itsred_v2.plaier.commands.LookAtCmd;
import net.itsred_v2.plaier.commands.PathFindCmd;
import net.itsred_v2.plaier.commands.SayCmd;
import net.itsred_v2.plaier.commands.StopTaskCmd;
import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.utils.Messenger;

public class CommandProcessor implements ChatOutputListener {

    private static final String COMMAND_TOKEN = ":";
    public static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    static {
        COMMAND_MAP.put("help", new HelpCommand());
        COMMAND_MAP.put("echo", new EchoCmd());
        COMMAND_MAP.put("say", new SayCmd());
        COMMAND_MAP.put("stoptask", new StopTaskCmd());
        COMMAND_MAP.put("lookat", new LookAtCmd());
        COMMAND_MAP.put("path", new PathFindCmd());
    }

    @Override
    public void onChatOutput(ChatOutputEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(COMMAND_TOKEN))
            return;

        event.cancel();

        message = message.substring(1); // removing the command token
        List<String> args = new ArrayList<>(List.of(message.split(" ")));

        String commandName = args.remove(0);
        Command cmd = COMMAND_MAP.get(commandName);

        if (cmd == null) {
            Messenger.chat("§cUnknown command: §6%s", commandName);
            Messenger.chat("§cType :help for a list of available commands.");
        } else {
            cmd.onCommand(args);
        }
    }

}
