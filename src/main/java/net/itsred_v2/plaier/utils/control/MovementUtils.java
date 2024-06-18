package net.itsred_v2.plaier.utils.control;

import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public class MovementUtils {

    public static GameOptions options() {
        return PlaierClient.MC.getOptions();
    }

    public static KeyBinding[] getControlList() {
        GameOptions options = options();
        return new KeyBinding[] {
                options.forwardKey,
                options.leftKey,
                options.rightKey,
                options.backKey,
                options.sneakKey,
                options.jumpKey,
                options.sprintKey
                // TODO : also lock attack and use key, and maybe more
        };
    }

    /**
     * When called, locks controls for this tick.
     */
    public static void lockControls() {
        for (KeyBinding key : getControlList()) {
            key.setPressed(false);
        }
    }

    // TODO: something to prevent autojump

    public static void forward(boolean bool) {
        options().forwardKey.setPressed(bool);
    }

    public static void setJumping(boolean bool) {
        options().jumpKey.setPressed(bool);
    }

    public static void setSprinting(boolean bool) {
        ClientPlayerEntity player = PlaierClient.getPlayer();
        player.setSprinting(bool);
    }

    public static void disableFlying() {
        ClientPlayerEntity player = PlaierClient.getPlayer();
        player.getAbilities().flying = false;
    }

}
