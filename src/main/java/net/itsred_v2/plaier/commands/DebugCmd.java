package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.utils.Messenger;

public class DebugCmd implements Command {

    @Override
    public void onCommand(List<String> args) {
        if (args.size() != 2) {
            Messenger.chat("§cSyntax:");
            Messenger.chat("§cdebug <enable | disable> <debugOption>");
            return;
        }

        boolean enable;
        switch (args.get(0)) {
            case "enable" -> enable = true;
            case "disable" -> enable = false;
            default -> {
                Messenger.chat("§cUnknown argument: §6%s§c. Try \"enable\" of \"disable\".", args.get(0));
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

}
