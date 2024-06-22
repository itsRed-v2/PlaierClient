package net.itsred_v2.plaier.utils.control;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.AutoJumpListener;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerController extends Toggleable implements AutoJumpListener {

    public final KeyboardController keyboard = new KeyboardController(PlaierClient.MC.getOptions());
    public final RotationBlocker rotation = new RotationBlocker();

    @Override
    protected void onEnable() {
        keyboard.enable();
        rotation.enable();
        PlaierClient.getEventManager().add(AutoJumpListener.class, this);
    }

    @Override
    protected void onDisable() {
        keyboard.disable();
        rotation.disable();
        PlaierClient.getEventManager().remove(AutoJumpListener.class, this);
    }

    @Override
    public void onAutoJump(AutoJumpEvent event) {
        event.cancel();
    }

    private static ClientPlayerEntity player() {
        return PlaierClient.getPlayer();
    }

    public void setSprinting(boolean bool) {
        player().setSprinting(bool);
    }

    public void disableFlying() {
        player().getAbilities().flying = false;
    }
}
