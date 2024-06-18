package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface AfterCameraUpdateListener extends Listener {

    void afterCameraUpdate(AfterCameraUpdateEvent event);

    class AfterCameraUpdateEvent extends Event<AfterCameraUpdateListener> {

        public boolean thirdPerson;

        public AfterCameraUpdateEvent(boolean thirdPerson) {
            this.thirdPerson = thirdPerson;
        }

        @Override
        public void fire(ArrayList<AfterCameraUpdateListener> listeners) {
            for (AfterCameraUpdateListener l : listeners) {
                l.afterCameraUpdate(this);
            }
        }

        @Override
        public Class<AfterCameraUpdateListener> getListenerType() {
            return AfterCameraUpdateListener.class;
        }
    }
}
