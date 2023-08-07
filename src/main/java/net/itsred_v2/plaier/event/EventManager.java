package net.itsred_v2.plaier.event;

import java.util.ArrayList;
import java.util.HashMap;

import net.itsred_v2.plaier.PlaierClient;

public class EventManager {

    public final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap = new HashMap<>();

    public static <L extends Listener, E extends Event<L>> void fire(E event) {
        PlaierClient.getEventManager().fireImpl(event);
    }

    public <L extends Listener, E extends Event<L>> void fireImpl(E event) {
        Class<L> listenerType = event.getListenerType();

        @SuppressWarnings("unchecked")
        ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(listenerType);

        if (listeners == null || listeners.isEmpty())
            return;

        // Creating a copy of the list to avoid concurrent modification issues.
        ArrayList<L> listenersCopy = new ArrayList<>(listeners);

        event.fire(listenersCopy);
    }

    public <L extends Listener> void add(Class<L> type, L listener) {
        @SuppressWarnings("unchecked")
        ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add(listener);
            listenerMap.put(type, listeners);
            return;
        }

        listeners.add(listener);
    }

    public <L extends Listener> void remove(Class<L> type, L listener) {
        @SuppressWarnings("unchecked")
        ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

        if (listeners != null) {
            listeners.remove(listener);
        }
    }
    
}