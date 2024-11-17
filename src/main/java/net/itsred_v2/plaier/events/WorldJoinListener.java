package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface WorldJoinListener extends Listener {

    void onWorldJoin();

    class WorldJoinEvent extends Event<WorldJoinListener> {

        public static final WorldJoinEvent INSTANCE = new WorldJoinEvent();

        @Override
        public void fire(ArrayList<WorldJoinListener> listeners) {
            for (WorldJoinListener l : listeners) {
                l.onWorldJoin();
            }
        }

        @Override
        public Class<WorldJoinListener> getListenerType() {
            return WorldJoinListener.class;
        }
    }
}
