package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.Collection;
import java.util.List;

public class DebugCmd extends Command {

    @Override
    public void onCommand(List<String> args) {
        if (args.size() != 2) {
            this.sendSyntaxErrorMessage();
            return;
        }

        boolean enable;
        switch (args.get(0)) {
            case "enable" -> enable = true;
            case "disable" -> enable = false;
            default -> {
                Messenger.chat("§cUnknown argument: §6%s§c. Try \"enable\" or \"disable\".", args.getFirst());
                return;
            }
        }

        switch (args.get(1)) {
            case "pressedKeys" -> PlaierClient.DEBUG_OPTIONS.pressedKeys(enable);
            case "targetNode" -> PlaierClient.DEBUG_OPTIONS.pathProcessorTargetNode(enable);
            case "*", "all" -> PlaierClient.DEBUG_OPTIONS.everything(enable);
            default -> {
                Messenger.chat("§cUnknown debug option: §6%s", args.get(1));
                Messenger.chat("§cAvailable debug options: §rpressedKeys, targetNode, all");
                return;
            }
        }

        if (List.of("*", "all").contains(args.get(1))) {
            Messenger.chat("%s every debug option.", enable ? "Enabled" : "Disabled");
        } else {
            Messenger.chat("%s debug option \"%s\"", enable ? "Enabled" : "Disabled", args.get(1));
        }
    }

    @Override
    public Collection<String> onTabComplete(List<String> args) {
        return switch (args.size()) {
            case 1 -> List.of("enable", "disable");
            case 2 -> List.of("pressedKeys", "targetNode", "all");
            default -> List.of();
        };
    }

    @Override
    public List<String> getHelp() {
        return List.of("Used to enable/disable debug options.");
    }

    @Override
    public List<String> getUse() {
        return List.of(":debug <enable | disable> <debugOption>");
    }

    @Override
    public List<String> getNames() {
        return List.of("debug");
    }

}
