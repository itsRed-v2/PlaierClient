package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.CancellableEvent;
import net.itsred_v2.plaier.event.Listener;

public interface ChatOutputListener extends Listener {

    void onChatOutput(ChatOutputEvent event);

    class ChatOutputEvent extends CancellableEvent<ChatOutputListener> {

        private final String message;

        public ChatOutputEvent(String message) {
            this.message = message;
        }

        @Override
        public void fire(ArrayList<ChatOutputListener> listeners) {
            for (ChatOutputListener l : listeners) {
                l.onChatOutput(this);
            }
        }

        public String getMessage() {
            return message;
        }

        @Override
        public Class<ChatOutputListener> getListenerType() {
            return ChatOutputListener.class;
        }
    }
}
