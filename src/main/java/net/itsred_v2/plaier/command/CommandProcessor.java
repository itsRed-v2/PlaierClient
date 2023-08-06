package net.itsred_v2.plaier.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.commands.EchoCmd;
import net.itsred_v2.plaier.commands.WalkCommand;
import net.itsred_v2.plaier.events.ChatOutputListener;

public class CommandProcessor implements ChatOutputListener {

    private static final String COMMAND_TOKEN = ":";
    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    static {
        COMMAND_MAP.put("echo", new EchoCmd());
        COMMAND_MAP.put("walk", new WalkCommand());
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
            PlaierClient.getCurrentSession().getMessenger().send("§cUnknown command: §6" + commandName);
        } else {
            cmd.onCommand(args);
        }
    }

}
