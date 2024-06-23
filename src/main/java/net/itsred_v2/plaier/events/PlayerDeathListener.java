package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface PlayerDeathListener extends Listener {

    void onPlayerDeath();

    class PlayerDeathEvent extends Event<PlayerDeathListener> {

        public static final PlayerDeathEvent INSTANCE = new PlayerDeathEvent();

        @Override
        public void fire(ArrayList<PlayerDeathListener> listeners) {
            for (PlayerDeathListener l : listeners) {
                l.onPlayerDeath();
            }
        }

        @Override
        public Class<PlayerDeathListener> getListenerType() {
            return PlayerDeathListener.class;
        }

    }
}
