package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.Event;
import net.itsred_v2.plaier.event.Listener;

public interface LeaveGameSessionListener extends Listener {

    void onLeaveGameSession();

    class LeaveGameSessionEvent extends Event<LeaveGameSessionListener> {

        @Override
        public void fire(ArrayList<LeaveGameSessionListener> listeners) {
            for (LeaveGameSessionListener l : listeners) {
                l.onLeaveGameSession();
            }
        }

        @Override
        public Class<LeaveGameSessionListener> getListenerType() {
            return LeaveGameSessionListener.class;
        }
    }
}
