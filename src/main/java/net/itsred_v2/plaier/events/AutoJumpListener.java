package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.CancellableEvent;
import net.itsred_v2.plaier.event.Listener;

public interface AutoJumpListener extends Listener {

    void onAutoJump(AutoJumpEvent event);

    class AutoJumpEvent extends CancellableEvent<AutoJumpListener> {

        @Override
        public void fire(ArrayList<AutoJumpListener> listeners) {
            for (AutoJumpListener l : listeners) {
                l.onAutoJump(this);
            }
        }

        @Override
        public Class<AutoJumpListener> getListenerType() {
            return AutoJumpListener.class;
        }

    }
}
