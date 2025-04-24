package net.itsred_v2.plaier.command;

import net.itsred_v2.plaier.events.ChatOutputListener;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements ChatOutputListener {

    public static final String COMMAND_TOKEN = ":";

    @Override
    public void onChatOutput(ChatOutputEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(COMMAND_TOKEN))
            return;

        event.cancel();

        message = message.substring(1); // removing the command token
        List<String> args = new ArrayList<>(List.of(message.split(" ")));

        String commandName = args.removeFirst();
        Command cmd = Commands.getByName(commandName);

        if (cmd == null) {
            Messenger.chat("§cUnknown command: §6%s", commandName);
            Messenger.chat("§cType :help for a list of available commands.");
        } else {
            cmd.onCommand(args);
        }
    }

}
