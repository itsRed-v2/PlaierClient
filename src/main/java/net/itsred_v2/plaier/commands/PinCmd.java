package net.itsred_v2.plaier.commands;

import com.mojang.authlib.GameProfile;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.PinHack;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PinCmd extends Command {

    private final PinHack pinHack = new PinHack();

    @Override
    public void onCommand(List<String> args) {
        if (args.size() != 1 && args.size() != 3) {
            this.sendSyntaxErrorMessage();
            return;
        }

        if (args.size() == 1) {
            Collection<PlayerListEntry> players = PlaierClient.MC.getNetworkHandler().getPlayerList();
            for (PlayerListEntry playerListEntry : players) {
                GameProfile gameProfile = playerListEntry.getProfile();
                if (gameProfile.getName().equalsIgnoreCase(args.get(0))) {
                    UUID playerUuid = gameProfile.getId();
                    if (pinHack.hasPlayer(playerUuid)) {
                        pinHack.removePlayer(playerUuid);
                        Messenger.chat("Unpinned %s.", gameProfile.getName());
                    } else {
                        pinHack.addPlayer(playerUuid);
                        Messenger.chat("Pinned %s.", gameProfile.getName());
                    }
                    return;
                }
            }

            Messenger.chat("§cNo player with the name §6" + args.get(0));
        }

        if (args.size() == 3) {
            int posX, posY, posZ;
            try {
                posX = Integer.parseInt(args.get(0));
                posY = Integer.parseInt(args.get(1));
                posZ = Integer.parseInt(args.get(2));
            } catch (NumberFormatException e) {
                Messenger.chat("§cUnable to parse coordinates.");
                return;
            }

            BlockPos pos = new BlockPos(posX, posY, posZ);
            if (pinHack.hasPosition(pos)) {
                pinHack.removePosition(pos);
                Messenger.chat("Unpinned %d %d %d.", posX, posY, posZ);
            } else {
                pinHack.addPosition(pos);
                Messenger.chat("Pinned %d %d %d.", posX, posY, posZ);
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(List<String> args) {
        if (args.size() == 1) {
            return PlaierClient.MC.getNetworkHandler()
                    .getPlayerList()
                    .stream()
                    .map(playerListEntry -> playerListEntry.getProfile().getName())
                    .toList();
        } else {
            return List.of();
        }
    }

    @Override
    public List<String> getHelp() {
        return List.of(
                "Allows pinning players or coordinates.",
                "Pinning draws a line from your crosshair to the projected position of the target on your screen, " +
                        "so you always know in which direction the target is."
        );
    }

    @Override
    public List<String> getUse() {
        return List.of(
                ":pin <player>",
                ":pin <x> <y> <z>"
        );
    }

    @Override
    public List<String> getNames() {
        return List.of("pin");
    }

}
