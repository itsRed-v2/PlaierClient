package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class OverlayCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        Messenger.overlay(String.join(" ", args));
    }

}
