package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface StartGameSessionListener extends Listener {

    void onStartGameSession();

    class StartGameSessionEvent extends Event<StartGameSessionListener> {

        @Override
        public void fire(ArrayList<StartGameSessionListener> listeners) {
            for (StartGameSessionListener l : listeners) {
                l.onStartGameSession();
            }
        }

        @Override
        public Class<StartGameSessionListener> getListenerType() {
            return StartGameSessionListener.class;
        }
    }
    
}
