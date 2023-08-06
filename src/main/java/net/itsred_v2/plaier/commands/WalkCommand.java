package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.minecraft.client.option.KeyBinding;

public class WalkCommand implements Command {

    @Override
    public void onCommand(List<String> args) {
        KeyBinding forwardKey = PlaierClient.MC.options.forwardKey;
        forwardKey.setPressed(!forwardKey.isPressed());
    }
    
}
