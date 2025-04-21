package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SayCmd extends Command {

    @Override
    public void onCommand(@NotNull List<String> args) {
        String message = String.join(" ", args);
        // This fakes the player sending a message through the chat
        PlaierClient.getPlayer().networkHandler.sendChatMessage(message);
    }

    @Override
    public List<String> getHelp() {
        return List.of("Says in the chat the provided message. Can be used to send messages which start with ':'.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":say <message>");
    }

    @Override
    public List<String> getNames() {
        return List.of("say");
    }

}