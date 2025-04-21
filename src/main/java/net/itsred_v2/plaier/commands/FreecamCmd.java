package net.itsred_v2.plaier.commands;

import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.FreecamHack;
import net.itsred_v2.plaier.utils.Messenger;

import java.util.List;

public class FreecamCmd extends Command {

    private final FreecamHack freecamHack = new FreecamHack();

    @Override
    public void onCommand(List<String> args) {
        if (args.size() > 1) {
            this.sendSyntaxErrorMessage();
            return;
        }

        if (args.isEmpty()) {
            toggleFreecam();
        } else {
            String firstArg = args.getFirst();
            if (firstArg.equals("switch") || firstArg.equals("s")) {
                switchControl();
            } else {
                Messenger.chat("§cUnknown argument: §e%s", firstArg);
                this.sendUse();
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
            Messenger.overlay("Freecam: Now controlling §bcamera§r.");
        } else {
            freecamHack.setControllingPlayer(true);
            Messenger.overlay("Freecam: Now controlling §6player§r.");
        }
    }

    @Override
    public List<String> getHelp() {
        return List.of(
                "Toggles the Freecam Hack.",
                "Passing 'switch' or just 's' switches between camera controls and " +
                        "player controls, all while keeping freecam enabled."
        );
    }

    @Override
    public List<String> getUse() {
        return List.of(
                ":freecam",
                ":freecam switch",
                ":freecam s"
        );
    }

    @Override
    public List<String> getNames() {
        return List.of("freecam", "fc");
    }

}
