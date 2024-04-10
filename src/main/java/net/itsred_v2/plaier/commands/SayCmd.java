package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import org.jetbrains.annotations.NotNull;

public class SayCmd implements Command {

    @Override
    public void onCommand(@NotNull List<String> args) {
        String message = String.join(" ", args);
        // This fakes the player sending a message through the chat
        PlaierClient.getPlayer().networkHandler.sendChatMessage(message);
    }

}