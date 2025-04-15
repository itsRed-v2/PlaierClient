package net.itsred_v2.plaier.commands;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.command.Command;
import net.itsred_v2.plaier.hacks.PinHack;
import net.itsred_v2.plaier.utils.Messenger;
import net.minecraft.client.network.PlayerListEntry;

public class PinCmd implements Command {

    private final PinHack pinHack = new PinHack();

    @Override
    public void onCommand(List<String> args) {
        if (args.size() != 1) {
            Messenger.chat("§cInvalid syntax.");
            return;
        }

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

}
