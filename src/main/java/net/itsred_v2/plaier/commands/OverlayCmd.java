package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class OverlayCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        Messenger.overlay(String.join(" ", args));
    }

    @Override
    public List<String> getHelp() {
        return List.of("Displays the given message as an overlay message.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":overlay <message>");
    }

    @Override
    public List<String> getNames() {
        return List.of("overlay");
    }

}
