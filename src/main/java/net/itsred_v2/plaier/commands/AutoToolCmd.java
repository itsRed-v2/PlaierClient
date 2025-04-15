package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.AutoToolHack;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class AutoToolCmd implements Command {

    private final AutoToolHack autoTool = new AutoToolHack();

    @Override
    public void onCommand(List<String> args) {
        if (autoTool.isEnabled()) {
            Messenger.chat("Disabled AutoTool.");
            autoTool.disable();
        } else {
            Messenger.chat("Enabled AutoTool.");
            autoTool.enable();
        }
    }

}
