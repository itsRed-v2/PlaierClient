package net.itsred_v2.plaier.utils.control;

import net.itsred_v2.plaier.PlaierClient;
import net.itsred_v2.plaier.events.ChangeLookDirectionListener;
import net.itsred_v2.plaier.utils.Toggleable;

public class RotationBlocker extends Toggleable implements ChangeLookDirectionListener {

    @Override
    protected void onEnable() {
        PlaierClient.getEventManager().add(ChangeLookDirectionListener.class, this);
    }

    @Override
    protected void onDisable() {
        PlaierClient.getEventManager().remove(ChangeLookDirectionListener.class, this);
    }

    @Override
    public void onChangeLookDirection(ChangeLookDirectionEvent event) {
        event.cancel();
    }

}
