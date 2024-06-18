package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface PreUpdateListener extends Listener {

    void onPreUpdate();

    class PreUpdateEvent extends Event<PreUpdateListener> {

        public static final PreUpdateEvent INSTANCE = new PreUpdateEvent();

        @Override
        public void fire(ArrayList<PreUpdateListener> listeners) {
            for (PreUpdateListener l : listeners) {
                l.onPreUpdate();
            }
        }

        @Override
        public Class<PreUpdateListener> getListenerType() {
            return PreUpdateListener.class;
        }
    }

}
