package net.itsred_v2.plaier.events;

import java.util.ArrayList;

import net.itsred_v2.plaier.event.CancellableEvent;
import net.itsred_v2.plaier.event.Listener;
import net.minecraft.client.option.KeyBinding;

public interface KeyListener extends Listener {

    void onKey(KeyEvent event);

    class KeyEvent extends CancellableEvent<KeyListener> {

        public final KeyBinding keyBinding;

        public KeyEvent(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
        }

        @Override
        public void fire(ArrayList<KeyListener> listeners) {
            for (KeyListener l : listeners) {
                l.onKey(this);
            }
        }

        @Override
        public Class<KeyListener> getListenerType() {
            return KeyListener.class;
        }

    }
}
