package net.itsred_v2.plaier.utils;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.rendering.hud.OverlayMessageHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class Messenger {

    private static final OverlayMessageHud OVERLAY_MESSAGE_HUD = new OverlayMessageHud();
    static {
        OVERLAY_MESSAGE_HUD.enable();
    }

    private static final String PREFIX = "§3Plaier §7§l» §f";

    public static void chat(String message, Object... formatArgs) {
        ClientPlayerEntity player = PlaierClient.getPlayer();
        message = message.formatted(formatArgs);
        player.sendMessage(Text.of(PREFIX + message), false);
    }

    public static void overlay(String message, Object... formatArgs) {
        OVERLAY_MESSAGE_HUD.setMessage(Text.of(message.formatted(formatArgs)));
    }

}
