package net.itsred_v2.plaier.utils;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class Messenger {

    private static final String PREFIX = "§3Plaier §7§l» §f";

    private final ClientPlayerEntity player;

    public Messenger(@NotNull ClientPlayerEntity player) {
        this.player = player;
    }

    public void send(String message, Object... formatArgs) {
        message = message.formatted(formatArgs);
        player.sendMessage(Text.of(PREFIX + message), false);
    }

    public void sendToServer(String message) {
        player.sendChatMessage(message);
    }

}
