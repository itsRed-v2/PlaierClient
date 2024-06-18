package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.FreecamHack;

public class FreecamCmd implements Command {

    private final FreecamHack freecamHack = new FreecamHack();

    @Override
    public void onCommand(List<String> args) {
        freecamHack.toggle();
    }

}
