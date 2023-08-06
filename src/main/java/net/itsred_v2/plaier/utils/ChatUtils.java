package net.itsred_v2.plaier.utils;

import java.util.Objects;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class ChatUtils {

    private static final String PREFIX = "§3Plaier §7§l» §f";

    public static void sendMessage(String message) {
        ClientPlayerEntity player = Objects.requireNonNull(PlaierClient.MC.player);
        Text text = Text.of(PREFIX + message);
        player.sendMessage(text, false);
    }

}
