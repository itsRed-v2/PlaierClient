package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.CancellableEvent;
import net.itsred_v2.plaier.event.Listener;

public interface ChangeLookDirectionListener extends Listener {

    void onChangeLookDirection(ChangeLookDirectionEvent event);

    class ChangeLookDirectionEvent extends CancellableEvent<ChangeLookDirectionListener> {

        public final double cursorDeltaX;
        public final double cursorDeltaY;

        public ChangeLookDirectionEvent(double cursorDeltaX, double cursorDeltaY) {
            this.cursorDeltaX = cursorDeltaX;
            this.cursorDeltaY = cursorDeltaY;
        }

        @Override
        public void fire(ArrayList<ChangeLookDirectionListener> listeners) {
            for (ChangeLookDirectionListener l : listeners) {
                l.onChangeLookDirection(this);
            }
        }

        @Override
        public Class<ChangeLookDirectionListener> getListenerType() {
            return ChangeLookDirectionListener.class;
        }
    }
}
