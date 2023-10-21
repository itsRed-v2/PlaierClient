package net.itsred_v2.plaier.utils.control;

import static net.itsred_v2.plaier.PlaierClient.MC;
import net.minecraft.client.option.KeyBinding;

public class MovementUtils {

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

    public static KeyBinding[] getControlList() {
        return new KeyBinding[] {
                MC.options.forwardKey,
                MC.options.leftKey,
                MC.options.rightKey,
                MC.options.backKey,
                MC.options.sneakKey,
                MC.options.jumpKey
        };
    }

}
