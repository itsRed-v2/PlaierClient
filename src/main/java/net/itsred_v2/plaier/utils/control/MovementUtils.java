package net.itsred_v2.plaier.utils.control;

import static net.itsred_v2.plaier.PlaierClient.MC;
import net.itsred_v2.plaier.PlaierClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;

public class MovementUtils {

    public static KeyBinding[] getControlList() {
        return new KeyBinding[] {
                MC.options.forwardKey,
                MC.options.leftKey,
                MC.options.rightKey,
                MC.options.backKey,
                MC.options.sneakKey,
                MC.options.jumpKey,
                MC.options.sprintKey
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
        MC.options.forwardKey.setPressed(bool);
    }

    public static void setJumping(boolean bool) {
        MC.options.jumpKey.setPressed(bool);
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
