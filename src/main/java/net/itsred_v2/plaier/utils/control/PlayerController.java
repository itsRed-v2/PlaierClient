package net.itsred_v2.plaier.utils.control;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.utils.Toggleable;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerController extends Toggleable {

    public final KeyboardController keyboard = new KeyboardController(PlaierClient.MC.getOptions());
    public final RotationController rotation = new RotationController();

    @Override
    protected void onEnable() {
        keyboard.enable();
        rotation.enable();
    }

    @Override
    protected void onDisable() {
        keyboard.disable();
        rotation.disable();
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
