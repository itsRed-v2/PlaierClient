package net.itsred_v2.plaier.commands;

import java.util.List;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.FreecamHack;
import net.itsred_v2.plaier.utils.Messenger;

public class FreecamCmd implements Command {

    private final FreecamHack freecamHack = new FreecamHack();

    @Override
    public void onCommand(List<String> args) {
        if (args.size() > 1) {
            Messenger.chat("§cInvalid syntax.");
            sendHelp();
            return;
        }

        if (args.isEmpty()) {
            toggleFreecam();
        } else {
            String firstArg = args.get(0);
            if (firstArg.equals("switch") || firstArg.equals("s")) {
                switchControl();
            } else {
                Messenger.chat("§cUnknown argument: §e%s", firstArg);
                sendHelp();
            }
        }
    }

    private void toggleFreecam() {
        if (freecamHack.isEnabled()) {
            freecamHack.disable();
        } else {
            freecamHack.enable();
        }
    }

    private void switchControl() {
        if (!freecamHack.isEnabled()) {
            Messenger.chat("§cFreecam must be enabled to do this.");
            return;
        }

        if (freecamHack.isControllingPlayer()) {
            freecamHack.setControllingPlayer(false);
            Messenger.chat("Freecam: Now controlling §bcamera§r.");
        } else {
            freecamHack.setControllingPlayer(true);
            Messenger.chat("Freecam: Now controlling §6player§r.");
        }
    }

    private void sendHelp() {
        Messenger.chat("Use:");
        Messenger.chat("§e:fc §7- §renable / disable freecam.");
        Messenger.chat("§e:fc switch §ror §e:fc s §7- §rswitch between camera control and player control.");
    }

}
